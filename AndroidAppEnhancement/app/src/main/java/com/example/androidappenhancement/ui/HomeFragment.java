package com.example.androidappenhancement.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidappenhancement.R;

/**
 * HomeFragment
 * -------------------------
 * Simple home screen after successful login.
 */
public class HomeFragment extends Fragment {

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView textWelcome = view.findViewById(R.id.textWelcome);
        textWelcome.setText("Welcome to the Home Screen!");

        return view;
    }
}