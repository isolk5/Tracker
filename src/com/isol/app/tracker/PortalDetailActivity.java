package com.isol.app.tracker;

import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PortalDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_portal_detail);
		
		//Recupero i dati del portale

		TextView tvPortalName = (TextView) this.findViewById(R.id.portalName);
		TextView tvPortalZone = (TextView) this.findViewById(R.id.portalZone);
		EditText edQta = (EditText) this.findViewById(R.id.qta);
		EditText tvNote = (EditText) this.findViewById(R.id.editTextNote);
		Spinner spStato = (Spinner) this.findViewById(R.id.spinnerStato);
		
		MyApplication myapp = MyApplication.getInstance();
		Intent intent = getIntent();		
		int item_ID = intent.getIntExtra(Constants.PAR_ITEM_ID, 0);
		
		int stato = 0;
		int portalZone = -1;
		boolean responsible = false;
		
		JSONObject obj = Utilita
				.getJSONObjectFromArray("JSONInterface.aspx?function=GetPortalDetail&groupID="
						+ myapp.getGroupID() + "&itemID=" + item_ID + "&personID=" + myapp.getPersonID());

		try {
			tvPortalName.setText(obj.getString("Item_Desc"));
			tvPortalZone.setText(obj.getString("Portal_Zone"));
			portalZone = obj.getInt("Portal_Zone_ID");
			tvNote.setText(obj.getString("Notes"));
			int iQta = obj.getInt("Qta");
			edQta.setText(String.valueOf(iQta));
			stato=obj.getInt("Stato");
			responsible=obj.getBoolean("Responsible");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		final String[] elencoVoci = { "Non importante", "Importante", "Da Uppare", "Megafield" };
		
		//Popolo lo spinner con un layout composto da icona + testo
		Spinner mySpinner = (Spinner)findViewById(R.id.spinnerStato);
		CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this,R.layout.spinner_stati_row, elencoVoci);
		mySpinner.setAdapter(adapter);
		spStato.setSelection(stato);
		
		if (responsible) {
			spStato.setEnabled(true);
			tvNote.setEnabled(true);
		} else {
			spStato.setEnabled(false);
			tvNote.setEnabled(false);
		}
		
//		edQta.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				
//	           	EditText etxt = (EditText)v;
//            	etxt.setText("");
//			}
//		});
//		
		edQta.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		        if(hasFocus) {
		        	EditText etxt = (EditText)v;
		        	etxt.setText("");
		        }
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
		newIntent.putExtra(Constants.PAR_FLAG_ITEM_LOCATION, 2);	
		startActivity(newIntent);

	}
	
	public void RicaricaClick(View v) {
		
		MyApplication myapp = MyApplication.getInstance();
		Intent intent = getIntent();		
		int item_ID = intent.getIntExtra(Constants.PAR_ITEM_ID, 0);

		Utilita.getJSONObjectFromArray("JSONInterface.aspx?function=UpdateCharge&groupID="
						+ myapp.getGroupID() + "&itemID=" + item_ID);
		
		Context context = getApplicationContext();
		CharSequence text = "Il portale è stato marcato come RICARICATO !";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
        //Metto la nuova quantita nell'itemt per l'ActivityResult
        intent.putExtra(Constants.PAR_ITEM_RECHARGED, "true");
        setResult(RESULT_OK, intent);
     }
	
	@SuppressWarnings("deprecation")
	public void AggiornaClick() {
		// TODO Auto-generated method stub
		
		EditText edQta = (EditText) this.findViewById(R.id.qta);
		String sQta = edQta.getText().toString();
		if (sQta.equals(""))
			sQta="0";

		EditText edNote = (EditText)this.findViewById(R.id.editTextNote);
		Spinner spStato = (Spinner)this.findViewById(R.id.spinnerStato);
		
		MyApplication myapp = MyApplication.getInstance();
		Intent intent = getIntent();		
		int item_ID = intent.getIntExtra(Constants.PAR_ITEM_ID, 0);

		Utilita.getJSONObjectFromArray("JSONInterface.aspx?function=Update&groupID="
						+ myapp.getGroupID() + "&itemID=" + item_ID + "&personID=" + myapp.getPersonID()
						+ "&FlagItemLocation=2&qta=" + sQta + "&Note=" + URLEncoder.encode(edNote.getText().toString()) + "&stato=" + spStato.getSelectedItemPosition());
		
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
        intent.putExtra(Constants.PAR_ITEM_STATO, spStato.getSelectedItemPosition());
        String sNote = edNote.getText().toString();
        intent.putExtra(Constants.PAR_ITEM_NOTE, sNote);
        setResult(RESULT_OK, intent);
        finish();
	}

	public void AttaccoClick(View v) {
		
		MyApplication myapp = MyApplication.getInstance();
		Intent intent = getIntent();		
		int item_ID = intent.getIntExtra(Constants.PAR_ITEM_ID, 0);

		Utilita.getJSONObjectFromArray("JSONInterface.aspx?function=Attack&groupID="
						+ myapp.getGroupID() + "&portalID=" + item_ID + "&personID=" + myapp.getPersonID());

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_save) {
			AggiornaClick();
		} else if (item.getItemId() == R.id.action_undo) {
			this.finish();
		}
		return true;
	}

}

class CustomSpinnerAdapter extends ArrayAdapter<String>  {

    LayoutInflater mInflater;
    private String[] dataReceived;

    public CustomSpinnerAdapter(Context ctx, int txtViewResourceId, String[] data) {
        super(ctx, txtViewResourceId, data);
        dataReceived =data;
        mInflater=LayoutInflater.from(ctx);
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.spinner_stati_row, null);
        }

        ((TextView) convertView.findViewById(R.id.descStato))
                .setText(dataReceived[position]);
        
        int imageResID  = -1;
        if (position==0) {
        	imageResID = R.drawable.rating0;
        } else if (position==1) {
        	imageResID = R.drawable.rating1;
        } else if (position==2) {
        	imageResID = R.drawable.rating2;
        } else if (position==3) {
        	imageResID = R.drawable.rating3;
        }
        
        ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(imageResID);
        
        return convertView;
    }
}
