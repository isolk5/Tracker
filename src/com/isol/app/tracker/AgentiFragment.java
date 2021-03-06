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
import android.widget.ListView;
import android.widget.TextView;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class AgentiFragment extends ListFragment {

	private OnFragmentInteractionListener mListener;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public AgentiFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Ottengo l'inventario
		MyApplication myapp = MyApplication.getInstance();

		JSONArray jArr = Utilita
				.getJSONArray("JSONInterface.aspx?function=GetPersonList&groupID="
						+ myapp.getGroupID());

		final ArrayList<AgentItemModel> theList = new ArrayList<AgentItemModel>();

		for (int i = 0; i < jArr.length(); i++) {
			JSONObject jObj;
			try {
				jObj = jArr.getJSONObject(i);
				AgentItemModel iim = new AgentItemModel();
				iim.agentID = jObj.getInt("PersonID");
				iim.nickname = jObj.getString("Person_Nickname");
				theList.add(iim);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		setListAdapter(new MySimpleArrayAdapter(getActivity(), theList));

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
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

//		if (null != mListener) {
//			// Notify the active callbacks interface (the activity, if the
//			// fragment is attached to one) that an item has been selected.
//			mListener
//					.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
//		}
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

	public class MySimpleArrayAdapter extends ArrayAdapter<AgentItemModel>  {
		
		private final Context context;
		private final ArrayList<AgentItemModel> values;

		public MySimpleArrayAdapter(Context context,
				ArrayList<AgentItemModel> values) {
			super(context, R.layout.agenteitem, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolderItem viewHolder;
			final AgentItemModel invItem = values.get(position);

			if (convertView == null) {

				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.agenteitem, parent,
						false);
				TextView tvNick = (TextView) convertView
						.findViewById(R.id.nickname);
				viewHolder = new ViewHolderItem();
				viewHolder.nickname = tvNick;
				
				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolderItem) convertView.getTag();
			}

			viewHolder.nickname.setText(invItem.nickname);
			convertView.setId(position);
			
			convertView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					//chiamo l'activity user_item
					Intent intent = new Intent(getActivity(), Item_User_List.class);
					MySimpleArrayAdapter theAdapter = (MySimpleArrayAdapter) getListAdapter();
					int position = v.getId();
					AgentItemModel iim = theAdapter.getItem(position);
					intent.putExtra(Constants.PAR_PERSON_ID, iim.agentID);	
					intent.putExtra(Constants.PAR_PERSON_NICKNAME, iim.nickname);	
					startActivity(intent);
				}
			});
			
			return convertView;
		}
		
	}

	public class AgentItemModel {
		public String nickname;
		public int agentID;
	}

	public class InventarioModel {
		public ArrayList<AgentItemModel> items;
	}

	static class ViewHolderItem {
		TextView nickname;
	}

}
