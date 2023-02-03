package com.research.myapplication.ui.home;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.research.myapplication.PieProgressDrawable;
import com.research.myapplication.R;
import com.research.myapplication.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ImageView imageView = binding.timeProgress;
        PieProgressDrawable pieProgressDrawable = new PieProgressDrawable();
        pieProgressDrawable.setColor(ContextCompat.getColor(root.getContext(), R.color.teal_200));
        pieProgressDrawable.setBackgroundColor(ContextCompat.getColor(root.getContext(), R.color.base));
        pieProgressDrawable.setBorderColor(ContextCompat.getColor(root.getContext(), R.color.base));
        final int[] i = {0, 0};
        imageView.setImageDrawable(pieProgressDrawable);
        new CountDownTimer(30000, 100) {
            @Override
            public void onTick(long l) {
                pieProgressDrawable.setLevel(i[0]);
                imageView.invalidate();
                i[1]++;
                if (i[1] % 3 == 0)
                    i[0]+=1;
            }

            @Override
            public void onFinish() {
                fadeOutAndHideImage(imageView);
            }
        }.start();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fadeOutAndHideImage(final ImageView img) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                img.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeOut);
    }
}