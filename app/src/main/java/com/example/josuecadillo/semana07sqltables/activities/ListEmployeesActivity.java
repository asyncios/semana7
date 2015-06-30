package com.example.josuecadillo.semana07sqltables.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.josuecadillo.semana07sqltables.R;
import com.example.josuecadillo.semana07sqltables.adapter.ListEmployeesAdapter;
import com.example.josuecadillo.semana07sqltables.dao.CompanyDAO;
import com.example.josuecadillo.semana07sqltables.dao.EmployeeDAO;
import com.example.josuecadillo.semana07sqltables.model.Company;
import com.example.josuecadillo.semana07sqltables.model.Employee;

import java.util.ArrayList;
import java.util.List;

public class ListEmployeesActivity extends Activity implements OnItemLongClickListener, OnItemClickListener, OnClickListener {

	public static final String TAG = "ListEmployeesActivity";

	public static final int REQUEST_CODE_ADD_EMPLOYEE = 40;
	public static final String EXTRA_ADDED_EMPLOYEE = "extra_key_added_employee";
	public static final String EXTRA_SELECTED_COMPANY_ID = "extra_key_selected_company_id";

	private ListView mListviewEmployees;
	private TextView mTxtEmptyListEmployees;
	private ImageButton mBtnAddEmployee;

	private ListEmployeesAdapter mAdapter;
	private List<Employee> mListEmployees;
	private EmployeeDAO mEmployeeDao;


    private EditText mTxtCompanyName;
    private EditText mTxtAddress;
    private EditText mTxtPhoneNumber;
    private EditText mTxtWebsite;
    private Button editButton;
    private Company company;
    private CompanyDAO mCompanyDao;

	private long mCompanyId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_employees);

		initViews();

		mEmployeeDao = new EmployeeDAO(this);
		Intent intent  = getIntent();
		if(intent != null) {
			this.mCompanyId = intent.getLongExtra(EXTRA_SELECTED_COMPANY_ID, -1);
		}

		if(mCompanyId != -1) {
			mListEmployees = mEmployeeDao.getEmployeesOfCompany(mCompanyId);
			if(mListEmployees != null && !mListEmployees.isEmpty()) {
				mAdapter = new ListEmployeesAdapter(this, mListEmployees);
				mListviewEmployees.setAdapter(mAdapter);
			}
			else {
				mTxtEmptyListEmployees.setVisibility(View.VISIBLE);
				mListviewEmployees.setVisibility(View.GONE);
			}
		}


        this.mCompanyDao = new CompanyDAO(this);
        company = mCompanyDao.getCompanyById(mCompanyId);

        mTxtCompanyName.setText(company.getName());
        mTxtAddress.setText(company.getAddress());
        mTxtPhoneNumber.setText(company.getPhoneNumber());
        mTxtWebsite.setText(company.getWebsite());
	}

	private void initViews() {
		this.mListviewEmployees = (ListView) findViewById(R.id.list_employees);
		this.mTxtEmptyListEmployees = (TextView) findViewById(R.id.txt_empty_list_employees);
		this.mBtnAddEmployee = (ImageButton) findViewById(R.id.btn_add_employee);
        this.editButton = (Button) findViewById(R.id.btn_edit_company);
		this.mListviewEmployees.setOnItemClickListener(this);
		this.mListviewEmployees.setOnItemLongClickListener(this);
		this.mBtnAddEmployee.setOnClickListener(this);

        this.mTxtCompanyName = (EditText) findViewById(R.id.txt_edit_company_name);
        this.mTxtAddress = (EditText) findViewById(R.id.txt_edit_address);
        this.mTxtPhoneNumber = (EditText) findViewById(R.id.txt_edit_phone_number);
        this.mTxtWebsite = (EditText) findViewById(R.id.txt_edit_website);

        this.editButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add_employee:
			Intent intent = new Intent(this, AddEmployeeActivity.class);
			startActivityForResult(intent, REQUEST_CODE_ADD_EMPLOYEE);
			break;
        case R.id.btn_edit_company:
             //editar la compania
            Editable companyName = mTxtCompanyName.getText();
            Editable address = mTxtAddress.getText();
            Editable phoneNumber = mTxtPhoneNumber.getText();
            Editable website = mTxtWebsite.getText();
            if (!TextUtils.isEmpty(companyName) && !TextUtils.isEmpty(address)
                    && !TextUtils.isEmpty(website)
                    && !TextUtils.isEmpty(phoneNumber)) {
                Company createdCompany = mCompanyDao.updateCompany(mCompanyId,
                        companyName.toString(), address.toString(),
                        website.toString(), phoneNumber.toString());

                Log.d(TAG, "compaia agregadsa : " + createdCompany.getName());
                Intent intentEdit = new Intent();
                //intentEdit.putExtra(ListCompaniesActivity.EXTRA_ADDED_COMPANY, createdCompany);
                setResult(RESULT_OK, intentEdit);
                Toast.makeText(this, R.string.company_created_successfully, Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                Toast.makeText(this, R.string.empty_fields_message, Toast.LENGTH_LONG).show();
            }
            break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE_ADD_EMPLOYEE) {
			if(resultCode == RESULT_OK) {
				if(mListEmployees == null || !mListEmployees.isEmpty()) {
					mListEmployees = new ArrayList<Employee>();
				}

				if(mEmployeeDao == null)
					mEmployeeDao = new EmployeeDAO(this);
				mListEmployees = mEmployeeDao.getEmployeesOfCompany(mCompanyId);
				
				if(mListEmployees != null && !mListEmployees.isEmpty() && 
						mListviewEmployees.getVisibility() != View.VISIBLE) {
					mTxtEmptyListEmployees.setVisibility(View.GONE);
					mListviewEmployees.setVisibility(View.VISIBLE);
				}
				
				if(mAdapter == null) {
					mAdapter = new ListEmployeesAdapter(this, mListEmployees);
					mListviewEmployees.setAdapter(mAdapter);
				}
				else {
					mAdapter.setItems(mListEmployees);
					mAdapter.notifyDataSetChanged();
				}
			}
		}
		else 
			super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mEmployeeDao.close();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Employee clickedEmployee = mAdapter.getItem(position);
		Log.d(TAG, "clickedItem : "+clickedEmployee.getFirstName()+" "+clickedEmployee.getLastName());
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Employee clickedEmployee = mAdapter.getItem(position);
		Log.d(TAG, "longClickedItem : "+clickedEmployee.getFirstName()+" "+clickedEmployee.getLastName());
		
		showDeleteDialogConfirmation(clickedEmployee);
		return true;
	}
	
	private void showDeleteDialogConfirmation(final Employee employee) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		 
        alertDialogBuilder.setTitle("Eliminar");
		alertDialogBuilder
				.setMessage("Seguro desea eliminar el empleado \""
						+ employee.getFirstName() + " "
						+ employee.getLastName() + "\"");
 
        // set positive button YES message
        alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// delete the employee and refresh the list
				if(mEmployeeDao != null) {
					mEmployeeDao.deleteEmployee(employee);
					
					//refresh the listView
					mListEmployees.remove(employee);
					if(mListEmployees.isEmpty()) {
						mListviewEmployees.setVisibility(View.GONE);
						mTxtEmptyListEmployees.setVisibility(View.VISIBLE);
					}

					mAdapter.setItems(mListEmployees);
					mAdapter.notifyDataSetChanged();
				}
				
				dialog.dismiss();
				Toast.makeText(ListEmployeesActivity.this, R.string.employee_deleted_successfully, Toast.LENGTH_SHORT).show();

			}
		});
        
        // set neutral button OK
        alertDialogBuilder.setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Dismiss the dialog
                dialog.dismiss();
			}
		});
        
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
	}
}
