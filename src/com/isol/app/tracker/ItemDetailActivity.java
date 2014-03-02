package com.isol.app.tracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ItemDetailActivity extends Activity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_detail);
		
		//Recupero i dati del portale

		TextView tvItemName = (TextView) this.findViewById(R.id.itemName);
		EditText edQta = (EditText) this.findViewById(R.id.qta);
		
		Intent intent = getIntent();
		String item_Name = intent.getStringExtra(Constants.PAR_ITEM_NAME);
		int qta = intent.getIntExtra(Constants.PAR_ITEM_QTA, 0);
		
		tvItemName.setText(item_Name);
		edQta.setText(String.valueOf(qta));
	
		edQta.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
	           	EditText etxt = (EditText)v;
            	etxt.setText("");
			}
		});
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.portal_detail, menu);
		return true;
	}

	public void PossessoriClick(View v) {
		
		Intent intent = getIntent();		
		int item_ID = intent.getIntExtra(Constants.PAR_ITEM_ID, 0);

		//Chiamo la user_item_List view
		Intent newIntent = new Intent(this, User_Item_List.class);
		newIntent.putExtra(Constants.PAR_ITEM_ID, item_ID);	
		newIntent.putExtra(Constants.PAR_FLAG_ITEM_LOCATION, 1);	
		startActivity(newIntent);

	}
	

	public void AggiornaClick(View v) {
		// TODO Auto-generated method stub
		
		EditText edQta = (EditText) this.findViewById(R.id.qta);
		String sQta = edQta.getText().toString();
		if (sQta=="")
			sQta="0";

		MyApplication myapp = MyApplication.getInstance();
		Intent intent = getIntent();		
		int item_ID = intent.getIntExtra(Constants.PAR_ITEM_ID, 0);

		Utilita.getJSONObjectFromArray("JSONInterface.aspx?function=Update&groupID="
						+ myapp.getGroupID() + "&itemID=" + item_ID + "&personID=" + myapp.getPersonID()
						+ "&FlagItemLocation=1&qta=" + sQta);
		
		Context context = getApplicationContext();
		CharSequence text = "Aggiornamento effettuato!";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
		//Nascondo l'eventuale tastiera
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edQta.getWindowToken(), 0);
        
        //Metto la nuova quantita nell'itemt per l'ActivityResult
        intent.putExtra(Constants.PAR_ITEM_QTA, Integer.valueOf(sQta));
        setResult(RESULT_OK, intent);
	}

}
