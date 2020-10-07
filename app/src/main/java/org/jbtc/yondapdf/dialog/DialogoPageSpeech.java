package org.jbtc.yondapdf.dialog;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jbtc.yondapdf.databinding.DialogPageSpeechBinding;
import org.jbtc.yondapdf.entidad.Book;


public class DialogoPageSpeech extends DialogFragment {

    public static final String tag = "dPageS";
    private static final String TAG = "lDialog";
    private DialogPageSpeechBinding binding;
    private Book book;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(int page);
    }
    private NoticeDialogListener listener;
    public void setNoticeDialogListener(NoticeDialogListener noticeDialogListener){
        listener=noticeDialogListener;
    }

    public DialogoPageSpeech(Book book) {
        this.book = book;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        binding = DialogPageSpeechBinding.inflate(inflater,container,false);

        binding.tvDialogMessage.setText("Paginas de 1 a "+book.getPages());

        binding.btDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String page = binding.tietDialogPagetext.getText().toString();
                if(page!=null&&!page.equals("")) {
                    try {
                        int pageInt = Integer.parseInt(page);
                        pageInt--;
                        if(0<=pageInt&&pageInt<book.getPages()) {
                            if (listener != null) listener.onDialogPositiveClick(pageInt);
                            dismiss();
                        }else{
                            //vibrar message
                            shake(binding.tvDialogMessage);
                        }
                    }catch (Exception e){e.printStackTrace();}
                }else{shake(binding.tvDialogMessage);}
            }
        });
        binding.btDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return binding.getRoot();
    }

    protected void shake(View view)
    {
        RotateAnimation rotate = new RotateAnimation(-5, 5,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setStartOffset(50);
        rotate.setRepeatMode(Animation.REVERSE);
        rotate.setInterpolator(new CycleInterpolator(5));
        view.startAnimation(rotate);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getDialog()!=null) {
            getDialog().getWindow().setLayout(
                    Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,250f,getResources().getDisplayMetrics())),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
