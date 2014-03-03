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
public class User_Item_List extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Ottengo le persone che posseggono questo item
		MyApplication myapp = MyApplication.getInstance();
		Intent intent = getIntent();
		
		int item_ID = intent.getIntExtra(Constants.PAR_ITEM_ID, 0);
		int FlagItemLocation = intent.getIntExtra(Constants.PAR_FLAG_ITEM_LOCATION, 0);
		
		JSONArray jArr = Utilita
				.getJSONArray("JSONInterface.aspx?function=GetUserItem&groupID="
						+ myapp.getGroupID() + "&itemID="+ item_ID + "&FlagItemLocation=" + FlagItemLocation);

		final ArrayList<UserItemModel> theList = new ArrayList<UserItemModel>();

		for (int i = 0; i < jArr.length(); i++) {
			JSONObject jObj;
			try {
				jObj = jArr.getJSONObject(i);
				UserItemModel uim = new UserItemModel();
				uim.person_desc = jObj.getString("Person_Desc");
				uim.person_id = jObj.getInt("Person_ID");
				uim.qta = jObj.getInt("Item_Quantity");
				theList.add(uim);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			
		//Dettagli dell'item
		JSONObject obj = Utilita
				.getJSONObjectFromArray("JSONInterface.aspx?function=GetItem&itemID="
						+ item_ID + "&groupID=" + myapp.getGroupID() + "&FlagItemLocation=" + FlagItemLocation);


		//Metto il titolo
		this.setTitle("Possessori di " + obj.getString("Item_Desc"));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

	public class MySimpleArrayAdapter extends ArrayAdapter<UserItemModel> {
		
		private final Context context;
		private final ArrayList<UserItemModel> values;

		public MySimpleArrayAdapter(Context context,
				ArrayList<UserItemModel> values) {
			super(context, R.layout.inventarioitem, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolderItem viewHolder;
			final UserItemModel invItem = values.get(position);

			if (convertView == null) {

				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.user_item, parent,
						false);
				TextView tvQta = (TextView) convertView
						.findViewById(R.id.textQta);
				TextView tvUser = (TextView) convertView
						.findViewById(R.id.textUser);
				viewHolder = new ViewHolderItem();
				viewHolder.user = tvUser;
				viewHolder.qta = tvQta;

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolderItem) convertView.getTag();
			}

			if (invItem != null) {
				viewHolder.user
						.setText(values.get(position).person_desc);
				viewHolder.qta
						.setText(Integer.toString(values.get(position).qta));
			}

			return convertView;
		}

	}

	public class UserItemModel {
		public String person_desc;
		public int person_id;
		public int item_id;
		public int qta;
	}

	public class InventarioModel {
		public ArrayList<UserItemModel> items;
	}

	static class ViewHolderItem {
		TextView user;
		TextView qta;
	}

}
