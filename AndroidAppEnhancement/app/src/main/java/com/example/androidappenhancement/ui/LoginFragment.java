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
 * LoginFragment
 * -------------------------
 * Handles secure user authentication using DatabaseHelper.
 */
public class LoginFragment extends Fragment {

    private EditText editUsername, editPassword;
    private DatabaseHelper db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        editUsername = view.findViewById(R.id.editUsername);
        editPassword = view.findViewById(R.id.editPassword);
        Button buttonLogin = view.findViewById(R.id.buttonLogin);
        Button buttonGoRegister = view.findViewById(R.id.buttonGoRegister);
        db = new DatabaseHelper(requireContext());

        buttonLogin.setOnClickListener(v -> loginUser(v));
        buttonGoRegister.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_to_register));

        return view;
    }

    private void loginUser(View v) {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            boolean success = db.authenticateUser(username, password);
            requireActivity().runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(v).navigate(R.id.action_login_to_home);
                } else {
                    Toast.makeText(requireContext(), "Invalid credentials.", Toast.LENGTH_SHORT).show();
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