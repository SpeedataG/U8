package com.speedata.uhf.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.utils.StringUtils;
import com.speedata.uhf.R;
import com.uhf.structures.SelectCriteria;

/**
 * Created by 张明_ on 2016/12/28.
 */

public class MaskDialog extends Dialog implements
        View.OnClickListener {

    private IUHFService iuhfService;
    private String current_tag_epc;
    private Context mContext;
    private Spinner spinnerArea;
    private EditText editTextAddr;
    private EditText editTextCount;
    private EditText editTextContent;
    private TextView textViewStatus;
    private Button btnMaskOk;
    private Button btnMaskCancel;
    private Button btnCancel;
    private static final String[] list = {"EPC", "TID", "USER"};

    public MaskDialog(Context context, IUHFService iuhfService, String current_tag_epc) {
        super(context);
        // TODO Auto-generated constructor stub
        this.iuhfService = iuhfService;
        this.current_tag_epc = current_tag_epc;
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mask);

        initView();
        getSelectCard();
    }

    /**
     * 获取掩码信息
     */
    private void getSelectCard() {
        SelectCriteria selectCard = iuhfService.getSelectCard();
        int bank = selectCard.bank - 1;
        spinnerArea.setSelection(bank);
        int offset = selectCard.offset;
        editTextAddr.setText(offset + "");
        int length = selectCard.length;
        editTextCount.setText(length + "");
        int byteLen = length / 8;
        if (length % 8 != 0) {
            byteLen++;
        }
        if (length == 0) {
            editTextContent.setHint("未设置掩码");
        } else {
            editTextContent.setText(StringUtils.byteToHexString(selectCard.maskData, byteLen));
        }

    }

    private void initView() {
        spinnerArea = (Spinner) findViewById(R.id.spinner_area);
        editTextAddr = (EditText) findViewById(R.id.editText_addr);
        editTextCount = (EditText) findViewById(R.id.editText_count);
        editTextContent = (EditText) findViewById(R.id.editText_content);
        textViewStatus = (TextView) findViewById(R.id.textView_status);
        btnMaskOk = (Button) findViewById(R.id.btn_mask_ok);
        btnMaskOk.setOnClickListener(this);
        btnMaskCancel = (Button) findViewById(R.id.btn_mask_cancel);
        btnMaskCancel.setOnClickListener(this);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (mContext, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArea.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == btnMaskOk) {
            //设置掩码
            selectCard();
        } else if (v == btnMaskCancel) {
            int cancelSelectCard = iuhfService.cancelSelectCard();
            if (cancelSelectCard == 0) {
                textViewStatus.setText("取消掩码成功");
            } else {
                textViewStatus.setText("取消掩码失败");
            }
        } else if (v == btnCancel) {
            dismiss();
        }
    }

    private void selectCard() {
        int area = (int) (spinnerArea.getSelectedItemId() + 1);
        int addr = Integer.parseInt(editTextAddr.getText().toString());
        int length = Integer.parseInt(editTextCount.getText().toString());
        String toString = editTextContent.getText().toString();
        byte[] content = StringUtils.stringToByte(toString);
        int selectCard = iuhfService.newSelectCard(area, addr, length, content);
        if (selectCard == 0) {
            textViewStatus.setText("设置掩码成功");
        } else {
            textViewStatus.setText("设置掩码失败");
        }
    }

}
