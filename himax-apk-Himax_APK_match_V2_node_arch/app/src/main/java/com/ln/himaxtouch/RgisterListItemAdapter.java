package com.ln.himaxtouch;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class RgisterListItemAdapter extends ArrayAdapter<String>  
{
	private final Context context;
	private final String[] Ids;
	private final int rowResourceId;

	public RgisterListItemAdapter(Context context, int textViewResourceId, String[] objects) 
	{
		super(context, textViewResourceId, objects);

		this.context = context;
		this.Ids = objects;
		this.rowResourceId = textViewResourceId;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(rowResourceId, parent, false);
		
		TextView textView = (TextView) rowView.findViewById(R.id.register_textView);
		EditText editText = (EditText) rowView.findViewById(R.id.register_editText);
		
		editText.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void afterTextChanged(Editable arg0) 
			{

				RegisterListDummyContent.ITEMS.get(position).content = arg0.toString();
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) 
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) 
			{
			}});
		
		textView.setText(RegisterListDummyContent.ITEMS.get(position).id);
		editText.setText(RegisterListDummyContent.ITEMS.get(position).content);
		
		return rowView;
	}
}
