package com.isol.app.tracker;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.isol.app.tracker.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class PortaliFragment extends ListFragment {

	private OnFragmentInteractionListener mListener;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PortaliFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ArrayList<InventarioItemModel> theList =getArrayList();

		setListAdapter(new MySimpleArrayAdapter(getActivity(), theList));

	}


	public ArrayList<InventarioItemModel> getArrayList() {
		
		// Ottengo l'inventario
		MyApplication myapp = MyApplication.getInstance();

		JSONArray jArr = Utilita
				.getJSONArray("JSONInterface.aspx?function=GetPortals&groupID="
						+ myapp.getGroupID() + "&personID="
						+ myapp.getPersonID());

		ArrayList<InventarioItemModel> theList = new ArrayList<InventarioItemModel>();

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
				iim.statoCarica=jObj.getInt("ChargeState");
				theList.add(iim);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return theList;
	
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onResume() {
		super.onResume();
		
	}

	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (null != mListener) {
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.
			mListener
					.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
		}
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        // Make sure the request was successful
	        if (resultCode == getActivity().RESULT_OK) {  
	        	
	        	//Verifico se si tratta di una ricarica
	        	String ricarica = data.getStringExtra(Constants.PAR_ITEM_RECHARGED);
	        	if (ricarica == null) {
	        		
		        	//aggiorno la quantita' come modificata nella activity di dettaglio
		        	int theQta = data.getIntExtra(Constants.PAR_ITEM_QTA, 0);
		        	int theIndex = data.getIntExtra(Constants.PAR_ARRAY_INDEX,-1);
		        	MySimpleArrayAdapter theAdapter = (MySimpleArrayAdapter)getListAdapter();
		        	theAdapter.getItem(theIndex).quantita=theQta;
		        	theAdapter.notifyDataSetChanged();
		        	
	        	} else {
	        		
		        	int theIndex = data.getIntExtra(Constants.PAR_ARRAY_INDEX,-1);
		        	MySimpleArrayAdapter theAdapter = (MySimpleArrayAdapter)getListAdapter();
		        	theAdapter.getItem(theIndex).statoCarica=4;
		        	theAdapter.notifyDataSetChanged();
	        	}
	        }
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
				TextView tvName = (TextView) convertView
						.findViewById(R.id.portalName);
				TextView tvZone = (TextView) convertView
						.findViewById(R.id.portalZone);
				TextView tvQta = (TextView) convertView
						.findViewById(R.id.qta);
				ImageView imStatoCarica = (ImageView) convertView
						.findViewById(R.id.imageCarica);
				viewHolder = new ViewHolderItem();
				viewHolder.portalName = tvName;
				viewHolder.portalZone = tvZone;
				viewHolder.qta = tvQta;
				viewHolder.FlagItemLocation = invItem.Flag_Item_Location;
				viewHolder.imgStatoCarica = imStatoCarica;
				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolderItem) convertView.getTag();
			}

			if (invItem != null) {
				viewHolder.portalName.setText(invItem.descrizione);
				viewHolder.portalZone.setText(invItem.Portal_Zone);
				viewHolder.qta.setText(Integer.toString(invItem.quantita));
				viewHolder.FlagItemLocation = invItem.Flag_Item_Location;
				
				//Mostro l'immagine corretta per la carica
				int imageResId = -1;
				if (invItem.statoCarica == 0) {
					imageResId = R.drawable.pila0;
				} else if (invItem.statoCarica == 1) {
					imageResId = R.drawable.pila1;
				} else if (invItem.statoCarica == 2) {
					imageResId = R.drawable.pila2;
				} else if (invItem.statoCarica == 3) {
					imageResId = R.drawable.pila3;
				} else if (invItem.statoCarica == 4) {
					imageResId = R.drawable.pila4;
				} 
				
				if (invItem.quantita > 0) {
					viewHolder.imgStatoCarica.setImageResource(imageResId);	
				}
			}

			convertView.setId(position);
			
			convertView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
//					//chiamo l'activity user_item
					Intent intent = new Intent(getActivity(), PortalDetailActivity.class);
                    final int ind = v.getId();
                    int item_ID = values.get(ind).item_id;
					int FlagItemLocation = values.get(ind).Flag_Item_Location;
					intent.putExtra(Constants.PAR_ITEM_ID, item_ID);	
					intent.putExtra(Constants.PAR_FLAG_ITEM_LOCATION, FlagItemLocation);	
					intent.putExtra(Constants.PAR_ARRAY_INDEX, ind);	
					startActivityForResult(intent,0);
				}
			});
			
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
		public int statoCarica;
	}

	public class InventarioModel {
		public ArrayList<InventarioItemModel> items;
	}

	static class ViewHolderItem {
		TextView portalName;
		TextView portalZone;
		TextView qta;
		ImageView imgStatoCarica;
		int FlagItemLocation;
	}

}
