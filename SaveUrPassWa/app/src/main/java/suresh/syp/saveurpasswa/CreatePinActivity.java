package suresh.syp.saveurpasswa;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by sureshsharma on 9/9/2017.
 */

public class CreatePinActivity extends AppCompatActivity {
    enum MessageStatus {
        ENTER_PIN,
        RE_ENTER_PIN,
        FINAL_ENTER_PIN,
        SUCCESS
    }

    int COUNT = 1;
    private TextView mMessage;
    private String mCurrentValue;
    private KeyboardView mKeyboardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pin);
        mMessage = (TextView) findViewById(R.id.tv_msg_enter_pin);
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        mKeyboardView = new KeyboardView(this);
        mainLayout.addView(mKeyboardView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mKeyboardView.mPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 4) {
                    if (mCurrentValue == null) {
                        setMessage(MessageStatus.RE_ENTER_PIN);
                        mCurrentValue = charSequence.toString();
                        COUNT++;
                    } else {
                        if (COUNT == 2) {
                            if (mCurrentValue.equals(charSequence.toString())) {
                                setMessage(MessageStatus.FINAL_ENTER_PIN);
                                mCurrentValue = charSequence.toString();
                                COUNT++;
                            } else {
                                mKeyboardView.mPasswordField.setError("Enter the same Pin");
                            }
                        } else if (COUNT == 3) {
                            if (mCurrentValue.equals(charSequence.toString())) {
                                setMessage(MessageStatus.SUCCESS);
                            } else {
                                mKeyboardView.mPasswordField.setError("Enter the same Pin");
                            }
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        findViewById(R.id.tv_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMessage(MessageStatus.ENTER_PIN);
            }
        });
    }

    void setMessage(MessageStatus status) {
        switch (status) {
            case ENTER_PIN:
                COUNT = 1;
                mMessage.setText(getString(R.string.message_enter_pin));
                mKeyboardView.mPasswordField.setText("");
                mCurrentValue = "";
                break;
            case RE_ENTER_PIN:
                mMessage.setText(getString(R.string.message_reenter_pin));
                mKeyboardView.mPasswordField.setText("");
                break;
            case FINAL_ENTER_PIN:
                mMessage.setText(getString(R.string.message_one_time_process));
                mKeyboardView.mPasswordField.setText("");
                break;
            case SUCCESS:
                createPin();
                break;
        }

    }

    @Override
    public void onBackPressed() {

    }

    private void createPin() {
        SQLiteDBUtil sqLiteDBUtil;
        sqLiteDBUtil = new SQLiteDBUtil(getApplicationContext());
        ArrayList<Object> pinData = new ArrayList<>();
        pinData.add(mKeyboardView.mPasswordField.getText().toString());
        sqLiteDBUtil.insertIntoPinTbl(pinData);
        Toast.makeText(this, "Pin created successfully : " + mKeyboardView.mPasswordField.getText().toString(), Toast.LENGTH_LONG).show();

        finish();
    }
}
