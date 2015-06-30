package com.example.josuecadillo.semana07sqltables.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.josuecadillo.semana07sqltables.R;
import com.example.josuecadillo.semana07sqltables.adapter.SpinnerCompaniesAdapter;
import com.example.josuecadillo.semana07sqltables.dao.CompanyDAO;
import com.example.josuecadillo.semana07sqltables.dao.EmployeeDAO;
import com.example.josuecadillo.semana07sqltables.model.Company;
import com.example.josuecadillo.semana07sqltables.model.Employee;

import java.util.List;

public class AddEmployeeActivity extends Activity implements OnClickListener, OnItemSelectedListener {

	public static final String TAG = "AddEmployeeActivity";

	private EditText mTxtFirstName;
	private EditText mTxtLastName;
	private EditText mTxtAddress;
	private EditText mTxtPhoneNumber;
	private EditText mTxtEmail;
	private EditText mTxtSalary;
	private Spinner mSpinnerCompany;
	private Button mBtnAdd;

	private CompanyDAO mCompanyDao;
	private EmployeeDAO mEmployeeDao;
	
	private Company mSelectedCompany;
	private SpinnerCompaniesAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_employee);

		initViews();

		this.mCompanyDao = new CompanyDAO(this);
		this.mEmployeeDao = new EmployeeDAO(this);
		
		//fill the spinner with companies
		List<Company> listCompanies = mCompanyDao.getAllCompanies();
		if(listCompanies != null) {
			mAdapter = new SpinnerCompaniesAdapter(this, listCompanies);
			mSpinnerCompany.setAdapter(mAdapter);
			mSpinnerCompany.setOnItemSelectedListener(this);
		}
	}

	private void initViews() {
		this.mTxtFirstName = (EditText) findViewById(R.id.txt_first_name);
		this.mTxtLastName = (EditText) findViewById(R.id.txt_last_name);
		this.mTxtAddress = (EditText) findViewById(R.id.txt_address);
		this.mTxtPhoneNumber = (EditText) findViewById(R.id.txt_phone_number);
		this.mTxtEmail = (EditText) findViewById(R.id.txt_email);
		this.mTxtSalary = (EditText) findViewById(R.id.txt_salary);
		this.mSpinnerCompany = (Spinner) findViewById(R.id.spinner_companies);
		this.mBtnAdd = (Button) findViewById(R.id.btn_add);

		this.mBtnAdd.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add:
			Editable firstName = mTxtFirstName.getText();
			Editable lastName = mTxtLastName.getText();
			Editable address = mTxtAddress.getText();
			Editable phoneNumber = mTxtPhoneNumber.getText();
			Editable email = mTxtEmail.getText();
			Editable strSalary = mTxtSalary.getText();
			mSelectedCompany = (Company) mSpinnerCompany.getSelectedItem();
			if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)
					&& !TextUtils.isEmpty(address) && !TextUtils.isEmpty(strSalary)
					&& !TextUtils.isEmpty(email) && mSelectedCompany != null
					&& !TextUtils.isEmpty(phoneNumber)) {
				// add the employee to database
				double salary = Double.valueOf(strSalary.toString());
				Employee createdEmployee = mEmployeeDao.createEmploye(firstName.toString(), lastName.toString(), address.toString(), email.toString(), phoneNumber.toString(), salary, mSelectedCompany.getId());
				Toast.makeText(this, R.string.employee_created_successfully, Toast.LENGTH_LONG).show();
				Log.d(TAG, "added employee : "+ createdEmployee.getFirstName()+" "+createdEmployee.getLastName()+", employee.companyId : "+createdEmployee.getCompany().getId());
				setResult(RESULT_OK);
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
	protected void onDestroy() {
		super.onDestroy();
		mCompanyDao.close();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		mSelectedCompany = mAdapter.getItem(position);
		Log.d(TAG, "selectedCompany : "+mSelectedCompany.getName());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
}
