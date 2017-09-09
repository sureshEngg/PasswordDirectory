package suresh.syp.saveurpasswa;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by sureshsharma on 8/28/2017.
 */

public class EditPasswordDetailsFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    public static String ARG_PARAM = "ARG_PARAM";
    public String[] mDataItem = null;
    private EditPasswordListener editPasswordListener;

    public static EditPasswordDetailsFragment newInstance(String[] passordItem) {
        EditPasswordDetailsFragment editStartEndTimeBottomSheetFragment = new EditPasswordDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray(ARG_PARAM, passordItem);
        editStartEndTimeBottomSheetFragment.setArguments(bundle);
        return editStartEndTimeBottomSheetFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_edit_password, container, false);
        TextView tvUsername = (TextView) v.findViewById(R.id.tv_username);
        TextView tvPassword = (TextView) v.findViewById(R.id.tv_password);
        mDataItem = getArguments().getStringArray(ARG_PARAM);
        tvUsername.setText(mDataItem[1]);
        tvPassword.setText(mDataItem[2]);

        ImageView ivEdit = (ImageView) v.findViewById(R.id.iv_edit);
        ImageView ivShare = (ImageView) v.findViewById(R.id.iv_share);
        ImageView ivDelete = (ImageView) v.findViewById(R.id.iv_delete);

        ivEdit.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_edit:
                dismiss();
                editPasswordListener.onClickEdit(mDataItem);
                break;
            case R.id.iv_share:
                shareUserNamePass();
                break;
            case R.id.iv_delete:
                openConfirmationDialog();
                break;
        }
    }

    private void shareUserNamePass() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p><b>Username:<b> " + mDataItem[1] + "<br><b>Password:</b> " + mDataItem[2] + "</p>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p><b>Username:<b> " + mDataItem[1] + "<br><b>Password:</b> " + mDataItem[2] + "</p>"));
        }
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    private void openConfirmationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        dismiss();
                        editPasswordListener.onDelete(mDataItem[4]);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        builder.setMessage("Are you sure to delete your password?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void setEditPasswordListener(EditPasswordListener editPasswordListener) {
        this.editPasswordListener = editPasswordListener;
    }

    public interface EditPasswordListener {
        void onClickEdit(String[] data);

        void onDelete(String id);
    }
}
