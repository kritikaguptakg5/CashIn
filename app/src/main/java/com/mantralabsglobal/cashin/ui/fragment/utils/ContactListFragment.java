/**
 * Copyright 2013 Ernestas Vaiciukevicius (ernestas.vaiciukevicius@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. * 
 */
package com.mantralabsglobal.cashin.ui.fragment.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.businessobjects.ContactResult;
import com.mantralabsglobal.cashin.ui.activity.app.ContactPickerActivity;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Ernestas Vaiciukevicius (ernestas.vaiciukevicius@gmail.com)
 *
 */
public class ContactListFragment extends Fragment implements LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
	private final static String SAVE_STATE_KEY = "mcListFrag";

	private final String[] projection = new String[] { Contacts._ID,
            Contacts.DISPLAY_NAME,
            Contacts.PHOTO_THUMBNAIL_URI };
	private final String selection = Contacts.HAS_PHONE_NUMBER + " = 1";
	
	private ListView mContactListView;
	private CursorAdapter mCursorAdapter;
	
	private class ContactsCursorAdapter extends SimpleCursorAdapter {
		public ContactsCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View ret = super.getView(position, convertView, parent);
			
			CheckBox checkbox = (CheckBox) ret.findViewById(R.id.contactCheck);
			
			getCursor().moveToPosition(position);
			String id = getCursor().getString(0);
			checkbox.setChecked(results.containsKey(id));
            ImageView imageView = (ImageView) ret.findViewById(R.id.contactImage);
            String imgUri = getCursor().getString(2);
            if(imgUri == null || imgUri.equals("")) {
                imageView.setImageResource(R.drawable.ic_contact_picture);
            }
			return ret; 
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putSerializable(SAVE_STATE_KEY, results);
	}

	private Hashtable<String, ContactResult> results = new Hashtable<String, ContactResult>();
	
	public Hashtable<String, ContactResult> getResults() {
		return results;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCursorAdapter = new ContactsCursorAdapter(getActivity(), R.layout.contact_list_item, null,
				new String[] { Contacts.DISPLAY_NAME,
                        Contacts.PHOTO_THUMBNAIL_URI },
				new int[] { R.id.contactLabel, R.id.contactImage }, 0);
		
		getLoaderManager().initLoader(0, null, this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (savedInstanceState != null) {
			results = (Hashtable<String, ContactResult>) savedInstanceState.getSerializable(SAVE_STATE_KEY);
		}
		
		View rootView = inflater.inflate(R.layout.contact_list_fragment, container);
		
		mContactListView = (ListView) rootView.findViewById(R.id.contactListView);
		
		mContactListView.setAdapter(mCursorAdapter);
		mContactListView.setOnItemClickListener(this);

		return rootView;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), Contacts.CONTENT_URI,
                projection, selection, null, Contacts.DISPLAY_NAME);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mCursorAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mCursorAdapter.swapCursor(null);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int pos, long rowId) {
		CheckBox checkbox = (CheckBox) view.findViewById(R.id.contactCheck);
		
		Cursor cursor = mCursorAdapter.getCursor();
		cursor.moveToPosition(pos);
		String id = cursor.getString(0);
        String name = cursor.getString(1);
		
		if (checkbox.isChecked()) {
			checkbox.setChecked(false);
			removeFromResult(id);
		} else if(canSelectMore()){

			checkbox.setChecked(true);
			
			Cursor itemCursor = getActivity().getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
					new String[] { id }, null);
			List<ContactResult.ResultItem> resultItems = new LinkedList<ContactResult.ResultItem>();

			itemCursorLoop:
			while (itemCursor.moveToNext()) {
				String contactNumber = itemCursor.getString(itemCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				int contactKind = itemCursor.getInt(itemCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

				for (ContactResult.ResultItem previousItem : resultItems) {
					if (contactNumber.equals(previousItem.getResult())) {
						continue itemCursorLoop;
					}
				}
				
				resultItems.add(new ContactResult.ResultItem(contactNumber, contactKind));
			}
			itemCursor.close();
			
			if (resultItems.size() > 1) {
				// contact has multiple items - user needs to choose from them
				chooseFromMultipleItems(resultItems, checkbox, id, name);
			} else {
				// only one result or all items are similar for this contact
				putInResult(id, new ContactResult(id,name,  resultItems));
			}
		}
        else
        {
            Toast.makeText(getActivity(),getString(R.string.max_reference_selection_reached), Toast.LENGTH_LONG).show();
        }
	}

    protected boolean canSelectMore()
    {
        return ((ContactPickerActivity)getActivity()).canSelectMore();
    }
	protected void putInResult(String key, ContactResult result)
	{
		results.put(key, result);
		((ContactPickerActivity)getActivity()).setSelectionCount(results.size());
	}


	protected void removeFromResult(String key)
	{
		results.remove(key);
		((ContactPickerActivity)getActivity()).setSelectionCount(results.size());
	}
	
	protected void chooseFromMultipleItems(List<ContactResult.ResultItem> items, CheckBox checkbox, String id, String name) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		ArrayList<String> itemLabels = new ArrayList<String>(items.size());
		
		for (ContactResult.ResultItem resultItem : items) {
			itemLabels.add(resultItem.getResult());
		}
		
		class ClickListener implements OnCancelListener, OnClickListener {
			private List<ContactResult.ResultItem> items;
			private CheckBox checkbox;
			private String id;
			private int checkedWhich;
            private String name;
			
			public ClickListener(List<ContactResult.ResultItem> items, CheckBox checkbox, String id, String name) {
				this.items = items;
				this.checkbox = checkbox;
				this.id = id;
                this.name = name;
                checkedWhich = 9999;
			}

			@Override
			public void onClick(DialogInterface dialog, int which) {
                if(which != DialogInterface.BUTTON_POSITIVE && which != DialogInterface.BUTTON_NEGATIVE && which!=DialogInterface.BUTTON_NEUTRAL)
                {
                    checkedWhich = which;
                }
                finish();
			}

			private void finish() {
				ArrayList<ContactResult.ResultItem> result = new ArrayList<ContactResult.ResultItem>(items.size());
                if(checkedWhich >=0 && checkedWhich != 9999)
                {
                    result.clear();
                    result.add(items.get(checkedWhich));
                }

				if (result.size() == 0) {
					checkbox.setChecked(false);
				} else {
					putInResult(id, new ContactResult(id, name, result));
				}
			}

			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
			
		}
		
		ClickListener clickListener = new ClickListener(items, checkbox, id, name);
		
		builder
                .setSingleChoiceItems(itemLabels.toArray(new String[0]),-1,clickListener)
			//.setMultiChoiceItems(itemLabels.toArray(new String[0]), null, clickListener)
                .setOnCancelListener(clickListener)
			.setPositiveButton(android.R.string.ok, clickListener)
			.show();
	}

}
