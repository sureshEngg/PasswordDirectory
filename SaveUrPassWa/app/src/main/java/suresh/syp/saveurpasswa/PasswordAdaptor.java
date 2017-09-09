package suresh.syp.saveurpasswa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sureshsharma on 8/28/2017.
 */

public class PasswordAdaptor extends BaseAdapter {

    private HomeActivity mContext;
    private ArrayList<String[]> mData;
    private LayoutInflater iv;
    private AdListener adListener;
    public PasswordAdaptor(HomeActivity context, ArrayList<String[]> data) {
        mContext = context;
        mData = data;
        adListener = context;
        iv = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup viewGroup) {
        ViewHandler viewHandler = null;
        if (convertView == null) {
            convertView = iv.inflate(R.layout.item_password, null);
            viewHandler = new ViewHandler();
            viewHandler.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHandler.tvUsername = (TextView) convertView.findViewById(R.id.tv_username);
            viewHandler.tvPassword = (TextView) convertView.findViewById(R.id.tv_password);
            convertView.setTag(viewHandler);
        } else {
            viewHandler = (ViewHandler) convertView.getTag();
        }

        final String[] data = mData.get(pos);
        viewHandler.tvTitle.setText(data[0]);
        viewHandler.tvUsername.setText(data[1]);
        viewHandler.tvPassword.setText(data[2]);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPasswordDetails(data);
                if (adListener != null) adListener.invoke();
            }
        });
        return convertView;
    }

    private void openPasswordDetails(String[] data) {
            final EditPasswordDetailsFragment editStartTimeOptionsFragment = EditPasswordDetailsFragment.newInstance(data);
            editStartTimeOptionsFragment.show(mContext.getSupportFragmentManager(), editStartTimeOptionsFragment.getTag());
            editStartTimeOptionsFragment.setEditPasswordListener(mContext);
    }

    class ViewHandler {
        TextView tvTitle;
        TextView tvUsername;
        TextView tvPassword;
    }

    interface AdListener {
        void invoke();
    }
}
