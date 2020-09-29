package org.jbtc.yondapdf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;

import org.jbtc.yondapdf.Utils;
import org.jbtc.yondapdf.database.RoomDatabaseBooksLN;
import org.jbtc.yondapdf.databinding.DialogPageSpeechBinding;
import org.jbtc.yondapdf.services.ServiceTTS;

public class DialogoPageSpeech extends DialogFragment {

    public static final String tag = "dPageS";
    DialogPageSpeechBinding binding;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(int page);
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
                int pageInt=Integer.parseInt(page);
                pageInt--;

                if(listener!=null)listener.onDialogPositiveClick(pageInt);
                dismiss();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {//todo:borrar:salle error pero ya no lo necesito xq los ago con un setter
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            Log.e("dialog", "onAttach: no importa este error, lo ago con un setter" );e.printStackTrace(); }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
