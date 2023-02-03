package com.research.myapplication.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.research.myapplication.R;
import com.research.myapplication.databinding.FragmentSlideshowBinding;

import java.security.SecureRandom;

public class SlideshowFragment extends Fragment {
    private int[] imageId = {R.drawable.circle_shape, R.drawable.circle_shape_2, R.drawable.circle_shape_3, R.drawable.circle_shape_4, R.drawable.circle_shape_5, R.drawable.circle_shape_6};

    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        int rand = new SecureRandom().nextInt(imageId.length);
        binding.ivCustom.setImageDrawable(ContextCompat.getDrawable(requireActivity().getApplicationContext(), imageId[rand]));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}