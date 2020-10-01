package org.jbtc.yondapdf.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.jbtc.yondapdf.R;
import org.jbtc.yondapdf.databinding.DialogPageSpeechBinding;
import org.jbtc.yondapdf.entidad.Book;

public class DialogoPageSpeech extends DialogFragment {

    public static final String tag = "dPageS";
    private static final String TAG = "lDialog";
    private DialogPageSpeechBinding binding;
    //private AlertDialog.Builder builder;
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

    /*
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateDialog: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_page_speech, null));

        //builder.setView(binding.getRoot())
                // Add action buttons
        .setTitle("numero")
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
                String page = binding.tietDialogPagetext.getText().toString();
                if(page!=null&&!page.equals("")) {
                    try {
                        int pageInt = Integer.parseInt(page);
                        pageInt--;
                        if (listener != null) listener.onDialogPositiveClick(pageInt);
                        dismiss();
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });* -//

        //Dialog d=super.onCreateDialog(savedInstanceState);
        //d.setTitle("numero");



        Dialog d = builder.create();
        WindowManager.LayoutParams params = d.getWindow().getAttributes();
        params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        d.getWindow().setAttributes(params);


        return d;
        //return builder.create();
        //return super.onCreateDialog(savedInstanceState);
    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        //return super.onCreateView(inflater, container, savedInstanceState);
        binding = DialogPageSpeechBinding.inflate(inflater,container,false);

        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //LayoutInflater inflater = requireActivity().getLayoutInflater();
        //builder.setView(inflater.inflate(R.layout.dialog_page_speech, null));

        /*builder.setView(binding.getRoot());
        Dialog d = builder.create();
        WindowManager.LayoutParams params = d.getWindow().getAttributes();
        params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        d.getWindow().setAttributes(params);*/

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

        /*
        getDialog().getWindow().setLayout(
                (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,800,getResources().getDisplayMetrics()),
                (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,800,getResources().getDisplayMetrics())
        );*/

        /*
        Dialog d =getDialog();
        //WindowManager.LayoutParams wmlp = d.getWindow().getAttributes();
        WindowManager.LayoutParams params = d.getWindow().getAttributes();
        params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        d.getWindow().setAttributes(params);
        */

        return binding.getRoot();
        //return d;
    }

    protected void shake(View view)
    {
        RotateAnimation rotate = new RotateAnimation(-5, 5,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setStartOffset(50);
        rotate.setRepeatMode(Animation.REVERSE);
        rotate.setInterpolator(new CycleInterpolator(5));
        //Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.animate_shake);
        //view.startAnimation(shake);
        view.startAnimation(rotate);
        //android.view.animation.Animation
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
        //(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,300f,getResources().getDisplayMetrics()),
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
