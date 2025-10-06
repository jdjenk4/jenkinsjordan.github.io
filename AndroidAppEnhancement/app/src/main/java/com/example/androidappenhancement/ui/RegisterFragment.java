package com.example.androidappenhancement.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.androidappenhancement.R;
import com.example.androidappenhancement.data.DatabaseHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RegisterFragment
 * -------------------------
 * Handles secure new user registration using DatabaseHelper.
 */
public class RegisterFragment extends Fragment {

    private EditText editUsername, editPassword;
    private DatabaseHelper db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        editUsername = view.findViewById(R.id.editUsernameRegister);
        editPassword = view.findViewById(R.id.editPasswordRegister);
        Button buttonRegister = view.findViewById(R.id.buttonRegisterUser);
        Button buttonGoLogin = view.findViewById(R.id.buttonGoLogin);
        db = new DatabaseHelper(requireContext());

        buttonRegister.setOnClickListener(this::registerUser);
        buttonGoLogin.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_register_to_login));

        return view;
    }

    private void registerUser(View v) {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            boolean success = db.addUser(username, password);
            requireActivity().runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(v).navigate(R.id.action_register_to_login);
                } else {
                    Toast.makeText(requireContext(), "Username already exists.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}