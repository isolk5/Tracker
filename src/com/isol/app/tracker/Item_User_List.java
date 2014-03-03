package com.isol.app.tracker;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class Item_User_List extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Ottengo le persone che posseggono questo item
		MyApplication myapp = MyApplication.getInstance();
		Intent intent = getIntent();
		
		int person_ID = intent.getIntExtra(Constants.PAR_PERSON_ID, 0);
		String person_Nickname = intent.getStringExtra(Constants.PAR_PERSON_NICKNAME);
		
		JSONArray jArr = Utilita
				.getJSONArray("JSONInterface.aspx?function=GetPortals&groupID="
						+ myapp.getGroupID() + "&personID="+ person_ID);

		final ArrayList<InventarioItemModel> theList = new ArrayList<InventarioItemModel>();

		for (int i = 0; i < jArr.length(); i++) {
			JSONObject jObj;
			try {
				jObj = jArr.getJSONObject(i);
				InventarioItemModel iim = new InventarioItemModel();
				iim.descrizione = jObj.getString("Item_Desc");
				iim.descrizioneTipo = jObj.getString("Item_Type_Desc");
				iim.quantita = jObj.getInt("Item_Quantity");
				iim.item_id = jObj.getInt("Item_ID");
				iim.Flag_Item_Location = jObj.getInt("Flag_Item_Location");
				iim.Portal_Zone = jObj.getString("Portal_Zone");
				if (iim.quantita > 0)
					theList.add(iim);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Ora carico anche gli altri item
		jArr = Utilita.getJSONArray("JSONInterface.aspx?function=GetInventory&groupID="
						+ myapp.getGroupID() + "&personID=" + person_ID);

		for (int i = 0; i < jArr.length(); i++) {
			JSONObject jObj;
			try {
				jObj = jArr.getJSONObject(i);
				InventarioItemModel iim = new InventarioItemModel();
				iim.descrizione = jObj.getString("Item_Desc");
				iim.descrizioneTipo = "";
				iim.quantita = jObj.getInt("Item_Quantity");
				iim.item_id = jObj.getInt("Item_ID");
				iim.Flag_Item_Location = jObj.getInt("Flag_Item_Location");
				iim.Portal_Zone = "";
				if (iim.quantita > 0)
					theList.add(iim);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//Metto il titolo
		this.setTitle("Inventario di " +person_Nickname);

		setListAdapter(new MySimpleArrayAdapter(this, theList));
		
	}

	// @Override
	// public void onAttach(Activity activity) {
	// super.onAttach(activity);
	// try {
	// mListener = (OnFragmentInteractionListener) activity;
	// } catch (ClassCastException e) {
	// throw new ClassCastException(activity.toString()
	// + " must implement OnFragmentInteractionListener");
	// }
	// }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(String id);
	}

	public class MySimpleArrayAdapter extends ArrayAdapter<InventarioItemModel> {
		
		private final Context context;
		private final ArrayList<InventarioItemModel> values;

		public MySimpleArrayAdapter(Context context,
				ArrayList<InventarioItemModel> values) {
			super(context, R.layout.portalitem, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolderItem viewHolder;
			final InventarioItemModel invItem = values.get(position);

			if (convertView == null) {

				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.portalitem, parent,
						false);
				TextView tvPortalName = (TextView) convertView
						.findViewById(R.id.portalName);
				TextView tvPortalZone = (TextView) convertView
						.findViewById(R.id.portalZone);
				TextView tvQta = (TextView) convertView
						.findViewById(R.id.qta);
				viewHolder = new ViewHolderItem();
				viewHolder.itemName = tvPortalName;
				viewHolder.portalZone = tvPortalZone;
				viewHolder.qta = tvQta;

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolderItem) convertView.getTag();
			}

			if (invItem != null) {
				viewHolder.itemName.setText(invItem.descrizione);
				viewHolder.portalZone.setText(invItem.Portal_Zone);
				viewHolder.qta.setText(Integer.toString(invItem.quantita));
			}

			return convertView;
		}

	}

	public class InventarioItemModel {
		public String descrizione; 	
		public String descrizioneTipo;
		public String Portal_Zone;
		public int quantita;
		public int item_id;
		public int Flag_Item_Location;
	}

	public class InventarioModel {
		public ArrayList<InventarioItemModel> items;
	}
	
	static class ViewHolderItem {
		TextView itemName;
		TextView portalZone;
		TextView qta;
	}

}
