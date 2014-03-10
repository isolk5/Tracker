package com.isol.app.tracker;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.isol.app.tracker.PortaliFragment.InventarioItemModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AgentProfileActivity extends Activity {

	public Spinner spinnerZone;
	public Spinner spinnerGruppi;
	public TextView tvNickname;
	public int personZone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agent_profile);
		
		//Recupero i dati del portale

		tvNickname = (TextView) this.findViewById(R.id.nickname);
		spinnerZone = (Spinner) this.findViewById(R.id.spinnerZone);
		spinnerGruppi = (Spinner) this.findViewById(R.id.spinnerGruppi);
		
		MyApplication myapp = MyApplication.getInstance();
		Intent intent = getIntent();		
		this.setTitle("Profilo Agente");
		boolean isFromSignin = intent.getBooleanExtra(Constants.PAR_FROM_SIGN_IN, true);
		
		//Recupero i settings
		PreferenceManager.setDefaultValues(this, R.xml.app_settings, false);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		Constants.serverAddress = sharedPref.getString("pref_server","");
		Constants.serverPort = Integer.valueOf(sharedPref.getString("pref_port", "80"));
		
		//Se debugger attivo, allora metto server di test
		boolean isBeingDebugged = android.os.Debug.isDebuggerConnected();
		if (isBeingDebugged) 
			Constants.serverAddress = "192.168.1.113/ITracker";
		
		if (Constants.serverPort == 80)
			Constants.serviceURL = "http://" + Constants.serverAddress + "/Services/";
		else
			Constants.serviceURL = "http://" + Constants.serverAddress + ":" + Constants.serverPort + "/Services/";
			
		//Questa chiamata censisce l'agente nel DB se non ancora presente
		JSONObject obj = Utilita
				.getJSONObjectFromArray("JSONInterface.aspx?function=GetPersonByAccount&account="
						+ myapp.getAccountName());

		String nickname = "";
		int GroupID = 0;
		
		try {
			 nickname = obj.getString("Person_Nickname");
			 GroupID = obj.getInt("Group_ID");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		//Se gruppo e zona valorizzati e provengo dal signin, allora procedo con l'activity principale
		if (GroupID != 0 && isFromSignin) {
	    	Intent newIntent = new Intent(this, Principale.class);
	    	startActivity(newIntent);
	    	//Tolgo la history a questa activity
	    	finish();
		}
		
		tvNickname.setText(nickname);
		
		//Popolo lo spinner dei gruppi
		JSONArray jArr = Utilita.getJSONArray("JSONInterface.aspx?function=GetGruppi");
		ArrayList<GroupItemModel> theList = new ArrayList<GroupItemModel>();
		int selectedIndex = 0;
		
		//Aggiunto elemento gruppo vuoto
		GroupItemModel gim = new GroupItemModel();
		gim.groupDesc = "";
		gim.groupID = 0;
		theList.add(gim);

		for (int i = 0; i < jArr.length(); i++) {
			JSONObject jObj;
			try {
				jObj = jArr.getJSONObject(i);
				gim = new GroupItemModel();
				gim.groupDesc = jObj.getString("Group_Desc");
				gim.groupID = jObj.getInt("Group_ID");
				theList.add(gim);
				if (gim.groupID == GroupID) {
					selectedIndex = i+1;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ArrayAdapter<GroupItemModel> adap = new ArrayAdapter<GroupItemModel> (this, android.R.layout.simple_spinner_item, theList);
		adap.setDropDownViewResource(android.R.layout.simple_spinner_item);
		spinnerGruppi.setAdapter(adap);
		spinnerGruppi.setSelection(selectedIndex);
		
		//Gestione della selezione
		spinnerGruppi.setOnItemSelectedListener(new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
			
			//Trovo l'item selezionato
			GroupItemModel gim = (GroupItemModel)parent.getItemAtPosition(pos);
			int GroupID = gim.groupID;
			
			//popolo lo spinner delle zone
			JSONArray jArr = Utilita.getJSONArray("JSONInterface.aspx?function=GetZonesByGroup&groupID=" + GroupID);
			ArrayList<ZoneItemModel> theZoneList = new ArrayList<ZoneItemModel>();
			
			//Aggiunto elemento gruppo vuoto
			ZoneItemModel zim = new ZoneItemModel();
			zim.zoneDesc = "";
			zim.zoneID = 0;
			theZoneList.add(zim);

			int selectedIndex = 0;
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject jObj;
				try {
					jObj = jArr.getJSONObject(i);
					zim = new ZoneItemModel();
					zim.zoneDesc = jObj.getString("Zone_Desc");
					zim.zoneID = jObj.getInt("Zone_ID");
					theZoneList.add(zim);
					if (zim.zoneID == personZone) {
						selectedIndex = i+1;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			ArrayAdapter<ZoneItemModel> adapZone = new ArrayAdapter<ZoneItemModel> (view.getContext(), android.R.layout.simple_spinner_item, theZoneList);
			adapZone.setDropDownViewResource(android.R.layout.simple_spinner_item);
			spinnerZone.setAdapter(adapZone);		
			spinnerZone.setSelection(selectedIndex);
		  }
		
		  @Override
		  public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		  }

		});
					
	}

	//Gestione dell'onclick sul bottone
	public void ConfermaClick(View v) {

		Context context = getApplicationContext();
		int duration = Toast.LENGTH_LONG;

		//Controllo se i campi sono popolati correttamente
		if (tvNickname.getText().equals("")) {
			CharSequence text = "ATTENZIONE - popolare il nome Agente";
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			return;
		}
		
		GroupItemModel selGim = (GroupItemModel)spinnerGruppi.getSelectedItem();			
		if (selGim.groupID==0) {
			CharSequence text = "ATTENZIONE - selezionare un gruppo di appartenenza";
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			return;
		}
		
		ZoneItemModel selZim = (ZoneItemModel)spinnerZone.getSelectedItem();	
		if (selZim.zoneID==0) {
			CharSequence text = "ATTENZIONE - selezionare una zona di appartenenza";
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			return;
		}
		
		//Memorizzo i dati selezionati
		MyApplication myapp = MyApplication.getInstance();
		Utilita.getJSONObjectFromArray("JSONInterface.aspx?function=AddPerson&groupID="
				+ selGim.groupID + "&zoneID=" + selZim.zoneID + "&nickname=" + tvNickname.getText() + "&accountName=" + myapp.getAccountName());

		//A questo punto passo all'activity principale
		Intent newIntent = new Intent(this, Principale.class);
		startActivityForResult(newIntent,0);
		finish();
		
	}
	
	public class GroupItemModel {
		public String groupDesc; 	
		public int groupID;
		
	     @Override
	     public String toString() {          
	         return  groupDesc;
	     }

	}
	public class ZoneItemModel {
		public String zoneDesc; 	
		public int zoneID;
		
	     @Override
	     public String toString() {          
	         return  zoneDesc;
	     }
	}

}
