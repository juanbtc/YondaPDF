package org.jbtc.yondapdf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import org.jbtc.yondapdf.Utils;
import org.jbtc.yondapdf.databinding.DialogPageSpeechBinding;
import org.jbtc.yondapdf.services.ServiceTTS;

public class DialogoPageSpeech extends DialogFragment {

    public static final String tag = "dPageS";
    DialogPageSpeechBinding binding;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(String page);
    }
    private NoticeDialogListener listener;
    public void setNoticeDialogListener(NoticeDialogListener noticeDialogListener){
        listener=noticeDialogListener;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        binding = DialogPageSpeechBinding.inflate(inflater,container,false);
        binding.btDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String page = binding.tietDialogPagetext.getText().toString();
                Intent iPlayIntent = new Intent(getContext(), ServiceTTS.class);
                iPlayIntent.setAction(Utils.ACTION_SEND_DATA);
                iPlayIntent.putExtra("page",page);
                ContextCompat.startForegroundService(getContext(), iPlayIntent);
                dismiss();
                //if(listener!=null)listener.onDialogPositiveClick(page);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) { e.printStackTrace(); }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
