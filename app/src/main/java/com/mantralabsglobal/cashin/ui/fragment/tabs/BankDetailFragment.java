package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.internal.util.Predicate;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.businessobjects.BankProvider;
import com.mantralabsglobal.cashin.service.AuthenticationService;
import com.mantralabsglobal.cashin.service.PerfiosService;
import com.mantralabsglobal.cashin.service.PrimaryBankService;
import com.mantralabsglobal.cashin.social.GoogleTokenRetrieverTask;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;
import com.mantralabsglobal.cashin.ui.activity.app.PerfiosActivity;
import com.mantralabsglobal.cashin.ui.fragment.utils.TabManager;
import com.mantralabsglobal.cashin.ui.view.BankDetailView;
import com.mantralabsglobal.cashin.utils.SMSProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by pk on 13/06/2015.
 */
public class BankDetailFragment extends BaseBindableFragment<List<PrimaryBankService.BankDetail>> {

    private static final String TAG = BankDetailFragment.class.getSimpleName();

    final private List<String> SCOPES = Arrays.asList(new String[]{
            "https://www.googleapis.com/auth/plus.login",
            "https://www.googleapis.com/auth/gmail.readonly"
    });

    PrimaryBankService primaryBankService;
    private ViewPager mViewPager;

    TabManager mTabManager;
    LinearLayout mainLayout, bank_detail_add_more;

    @InjectView(R.id.bank_statement_bar)
    ViewGroup bankStatementInfoBar;

    @InjectView(R.id.vg_bank_details)
    ViewGroup vg_bank_details;

    @InjectView(R.id.already_have_statements)
    ViewGroup bankStatementsPresentView;

    @InjectView(R.id.eStatement)
    Button eStatement;

    @InjectView(R.id.netBanking)
    Button netBanking;

    @InjectView(R.id.ic_edit)
    ImageView edit_view;

    @InjectView(R.id.info_bar)
    ViewGroup vgInfoBar;
    private TabHost mTabHost;

    List<BankDetailView> bankDetailViewList;
    List<String> bankList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabManager = new TabManager(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_bank_detail, container, false);

        mainLayout = (LinearLayout) view.findViewById(R.id.bank_statement);

        bank_detail_add_more = (LinearLayout) view.findViewById(R.id.bank_detail);

        mTabHost = mTabManager.handleCreateView(view);

        mTabManager.addTab(mTabHost.newTabSpec("blank").setIndicator("Blank Fragment"),
                BlankFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("net_banking").setIndicator("Net Banking"),
                NetBankingFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("e_statement").setIndicator("E-Statement from Gmail"),
                EStatementFragment.class, null);

        mTabHost.setCurrentTab(0);
        mTabHost.getTabWidget().getChildAt(0).setVisibility(View.GONE);

        return view;
    }

    @OnClick(R.id.netBanking)
    public void netBankingClick() {
        mTabHost.setCurrentTab(1);
        netBanking.setSelected(true);
        eStatement.setSelected(false);
        //Load perfios webview
        Intent intent = new Intent(getActivity(), PerfiosActivity.class);
        getActivity().startActivityForResult(intent, BaseActivity.PERFIOS_NET_BANKING);
        reset(false);
        // netBanking.setEnabled(false);
        //netBanking.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.bank_statements_button_disable));

    }

    @OnClick(R.id.eStatement)
    public void eStatementClick() {
        mTabHost.setCurrentTab(2);
        netBanking.setSelected(false);
        eStatement.setSelected(true);

        scanGmailForBankStatements();

    }

    @Override
    protected View getFormView() {
        return vg_bank_details;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        primaryBankService = ((Application) getActivity().getApplication()).getRestClient().getPrimaryBankService();
        reset(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        netBanking.setSelected(false);
        eStatement.setSelected(false);
        save();
    }

    @Override
    protected void onCreate(List<PrimaryBankService.BankDetail> updatedData, Callback<List<PrimaryBankService.BankDetail>> saveCallback) {
        primaryBankService.createPrimaryBankDetail(updatedData, saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<List<PrimaryBankService.BankDetail>> dataCallback) {
        primaryBankService.getPrimaryBankDetail(dataCallback);
    }

    @OnClick(R.id.btn_add_more)
    public void addMoreInBankStatement() {
        if (bankDetailViewList == null || bankDetailViewList.size() == 0 || (bankDetailViewList.get(bankDetailViewList.size() - 1).getBankDetail().getAccountNumber() != null
                && bankDetailViewList.get(bankDetailViewList.size() - 1).getBankDetail().getAccountNumber().trim().length() > 0)) {

            if (bankDetailViewList == null)
                bankDetailViewList = new ArrayList<BankDetailView>();

            PrimaryBankService.BankDetail bankDetail = new PrimaryBankService.BankDetail();
            onCreateDialog(bankDetail);
        } else {
            Toast.makeText(getActivity(), "Please enter account number in previous row!", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.ic_edit)
    public void editPrimaryBank() {
        bank_detail_add_more.setVisibility(View.VISIBLE);
        vgInfoBar.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
        edit_view.setVisibility(View.GONE);
        vg_bank_details.removeAllViews();
        for (BankDetailView bdView : bankDetailViewList) {
            if (bdView.getBankDetail().isPrimary()) {
                bdView.getBankDetail().setIsPrimary(false);
                bdView.updateUI();
            }
            vg_bank_details.addView(bdView);
        }
    }

    @OnClick(R.id.statement_edit)
    public void editBankStatement() {
        bankStatementsPresentView.setVisibility(View.GONE);
        bankStatementInfoBar.setVisibility(View.VISIBLE);
        mTabHost.setVisibility(View.VISIBLE);
        //((Application) getActivity().getApplication()).setGmailAccount(null);

    }

    public void onCreateDialog(final PrimaryBankService.BankDetail bankDetail) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Bank");
        bankList = BankProvider.getInstance().getBanks().getBankNameList();
        Collections.sort(bankList);
        builder.setItems(bankList.toArray(new String[]{}), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String bankName = bankList.get(item);
                Log.d("Bank name", bankName);
                dialog.dismiss();
                bankDetail.setBankName(bankName);
                addMoreBankDetail(bankDetail);
            }
        });
        builder.show();
    }

    private void addMoreBankDetail(final PrimaryBankService.BankDetail bankDetail) {

        final BankDetailView view = new BankDetailView(getActivity());
        view.setBankDetail(bankDetail);
        view.accountNumber.requestFocus();
        vg_bank_details.addView(view);
        bankDetailViewList.add(view);
        addMoreAccNumberListener(view);
    }

    @Override
    protected void handleDataNotPresentOnServer() {
        final SMSProvider provider = new SMSProvider(getActivity());
        SMSProvider.ReadBankAccountInfoTask task = new SMSProvider.ReadBankAccountInfoTask(new Predicate<SMSProvider.SMSMessage>() {
            @Override
            public boolean apply(SMSProvider.SMSMessage smsMessage) {
                return provider.isSenderBank(smsMessage) && provider.hasAccountInformation(smsMessage);
            }
        }, provider) {

            @Override
            protected void onProgressUpdate(String... values) {
                /*showProgressDialog2(values[0]); */
            }

            @Override
            protected void onPostExecute(List<PrimaryBankService.BankDetail> bankDetailList) {
                dismissProgressDialog2();
                bindDataToForm(bankDetailList);
            }
        };
        // showProgressDialog2("Scanning SMS");
        task.execute(Long.MIN_VALUE);
    }

    @Override
    protected void onUpdate(List<PrimaryBankService.BankDetail> updatedData, Callback<List<PrimaryBankService.BankDetail>> saveCallback) {
        primaryBankService.createPrimaryBankDetail(updatedData, saveCallback);
    }

    public void disableStatementsUploadededSuccessfully() {

        bankStatementsPresentView.setVisibility(View.VISIBLE);
        bankStatementInfoBar.setVisibility(View.GONE);
        mTabHost.setVisibility(View.GONE);
    }

    @Override
    public void bindDataToForm(final List<PrimaryBankService.BankDetail> value) {
        if (value != null) {

            bankDetailViewList = new ArrayList<>();
            BankDetailView primaryBankView = null;
            for (final PrimaryBankService.BankDetail bankDetail : value) {
                if(getActivity() != null) {
                    BankDetailView view = new BankDetailView(getActivity());
                    view.setBankDetail(bankDetail);
                    bankDetailViewList.add(view);
                    primaryBankChangeListener(view);
                    if (bankDetail.isPrimary())
                        primaryBankView = view;
                }
            }

            vg_bank_details.removeAllViews();

            if(primaryBankView != null){
                vg_bank_details.addView(primaryBankView);
                edit_view.setVisibility(View.VISIBLE);
                bank_detail_add_more.setVisibility(View.GONE);
                vgInfoBar.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);

                if (primaryBankView.getBankDetail().isUserStatementPresent())
                    disableStatementsUploadededSuccessfully();

            }
            else
            {
                for (final BankDetailView bankDetailview : bankDetailViewList) {
                    vg_bank_details.addView(bankDetailview);
                }
            }
        }
    }

    private void primaryBankChangeListener(BankDetailView view) {
        view.addPrimaryFlagChangeListener(new BankDetailView.PrimaryFlagChangedListener() {
            @Override
            public void onPrimaryChanged(BankDetailView bankDetailView) {
                bankDetailView.getBankDetail().setIsPrimary(true);
                bankDetailView.updateUI();
                handlePrimaryBankSelected(bankDetailView);
            }
        });
    }

    private void addMoreAccNumberListener(final BankDetailView view) {
        view.addAddMoreAccountNumberListener(new BankDetailView.AddMoreAccountNumberListener() {
            @Override
            public void onAccountNumberChanged(BankDetailView bankDetailView) {
                InputMethodManager imm = (InputMethodManager) (getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
                imm.hideSoftInputFromWindow(view.accountNumber.getWindowToken(), 0);
                view.accountNumber.clearFocus();
                primaryBankChangeListener(bankDetailView);
            }
        });
    }

    private void handlePrimaryBankSelected(BankDetailView bankDetailView) {

        edit_view.setVisibility(View.VISIBLE);
        bank_detail_add_more.setVisibility(View.GONE);
        vgInfoBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        vg_bank_details.removeAllViews();
        vg_bank_details.addView(bankDetailView);
        save();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      //  if (mTabManager.getCurrentFragment() != null)
      //      mTabManager.getCurrentFragment().onActivityResult(requestCode, resultCode, data);

        if (requestCode == BaseActivity.PERFIOS_NET_BANKING && resultCode == Activity.RESULT_OK) {
            PerfiosService.PerfiosStatusUploadTask task = new PerfiosService.PerfiosStatusUploadTask(getActivity()) {
                @Override
                protected void onProgressUpdate(String... values) {
                    showProgressDialog2(values[0]);
                }

                @Override
                protected void onPostExecute(PrimaryBankService.PerfiosTransactionResponse result) {
                    dismissProgressDialog2();
                    if (exception != null)
                        showToastOnUIThread("Error:" + exception.getMessage());
                    else
                        showToastOnUIThread("Statements successfully retreived!");
                }
            };
            showProgressDialog2("Checking status");
            task.execute(data.getStringExtra("transactionId"));
        }
        else if (requestCode == BaseActivity.PICK_ACCOUNT_REQUEST && resultCode == Activity.RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Log.d(TAG, "Account Name=" + accountName);
            //Uncomment below line to remember the gmail account
            Application.getInstance().setGmailAccount(accountName);
            requestForGmailToken(accountName);
        }
        else if (requestCode == BaseActivity.REQ_SIGN_IN_REQUIRED && resultCode == Activity.RESULT_OK) {
            requestForGmailToken(Application.getInstance().getGmailAccount());
            //scanGmailForBankStatements();
        }
    }

    protected void scanGmailForBankStatements() {
        Intent googlePicker = AccountPicker.newChooseAccountIntent(null, null,
                new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null);
        getActivity().startActivityForResult(googlePicker, BaseActivity.PICK_ACCOUNT_REQUEST);
    }

    protected void requestForGmailToken(final String gmailAccount) {
        new GoogleTokenRetrieverTask() {

            @Override
            protected String getEmail() {
                return gmailAccount;
            }

            protected String getScope()
            {
                return String.format("oauth2:server:client_id:%s:api_scope:%s", getResources().getString(R.string.server_client_id), TextUtils.join(" ", SCOPES));

                //              return super.getScope() + " " + GMAIL_SCOPE;
            }

            @Override
            public void onException(UserRecoverableAuthException e) {
                getActivity().startActivityForResult(e.getIntent(), BaseActivity.REQ_SIGN_IN_REQUIRED);
            }

            @Override
            public void onException(IOException e) {
                super.onException(e);
                showErrorOnUIWithoutAction(R.string.io_exception);
                hideProgressDialog();
            }

            @Override
            public void onException(GoogleAuthException e) {
                super.onException(e);
                showErrorOnUIWithoutAction(R.string.google_authentication_exception);
                hideProgressDialog();
            }

            @Override
            protected void afterTokenRecieved(String email, String token) {
                hideProgressDialog();
                AuthenticationService.UserGoogleAuthCode authCode = new AuthenticationService.UserGoogleAuthCode();
                authCode.setAuthCode(token);
                authCode.setEmail(email);
                showToastOnUIThread("Successfully Retrieved!");
                getCashInApplication().getRestClient().getAuthenticationService().sendGoogleAuthCode(authCode, new Callback<AuthenticationService.UserStatementStatus>() {
                    @Override
                    public void success(AuthenticationService.UserStatementStatus userStatementStatus, Response response) {
                        Log.i(TAG, "token posted to server");
                        showToastOnUIThread(userStatementStatus.getMessage());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // showToastOnUIThread(error.getMessage());
                    }
                });
                Log.d(TAG, token);
            }
        }.execute(getActivity());
    }

    @Override
    public List<PrimaryBankService.BankDetail> getDataFromForm(List<PrimaryBankService.BankDetail> base) {
        if (base == null) {
            base = getBankDetailList(bankDetailViewList);

        }
        /*base.setBankName(bankName.getText().toString());
        base.setAccountNumber(accountNumber.getText().toString());*/
        return base;
    }

    public List<PrimaryBankService.BankDetail> getBankDetailList(List<BankDetailView> bankDetailViewList) {
        List<PrimaryBankService.BankDetail> bankDetailList = new ArrayList<PrimaryBankService.BankDetail>();
        if (bankDetailViewList != null)
            for (int i = 0; i < bankDetailViewList.size(); i++)
                bankDetailList.add(bankDetailViewList.get(i).getBankDetail());

        return bankDetailList;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (mTabManager != null)
            mTabManager.handleViewStateRestored(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabManager.handleDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mTabManager.handleSaveInstanceState(outState);
    }

    @Override
    public boolean isFormValid() {
        List<PrimaryBankService.BankDetail> bankDetailList = getDataFromForm(null);
        boolean bankDataPresent = false;

        if(bankDetailList.size() >= 1)
            bankDataPresent = true;

       for( PrimaryBankService.BankDetail bankDetail : bankDetailList ) {
           bankDataPresent = bankDataPresent && !TextUtils.isEmpty(bankDetail.getBankName())
                   && !TextUtils.isEmpty(bankDetail.getAccountNumber());
       }

        return bankDataPresent;
    }

}
