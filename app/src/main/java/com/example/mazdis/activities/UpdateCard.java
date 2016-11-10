package com.example.mazdis.activities;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.stripe.android.*;

import com.example.mazdis.sabps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

import java.util.HashMap;
import java.util.Map;

public class UpdateCard extends DialogFragment implements View.OnClickListener {

    private static final String MONTH_SPINNER_DEFAULT_VALUE = "Month";
    private static final String YEAR_SPINNER_DEFAULT_VALUE = "Year";
    private static final String STRIPE_TEST_PUBLISHABLE_KEY = "pk_test_rkbHNq1c430fnUTDRegoZhCM";
    private static final String FIREBASE_NEW_CUSTOMER = "new customer";
    private static final String FIREBASE_CUSTOMER = "customer";
    private static final String FIREBASE_TOKEN_ID = "tokenId";
    private static final String FIREBASE_USER_ID = "uid";
    private static final String FIREBASE_USER_EMAIL = "email";
    EditText cardNumber;
    EditText cvc;
    Spinner month;
    Spinner year;

    DatabaseReference mDatabase;
    FirebaseAuth mAuth;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.activity_update_card, null, false);

        cardNumber = (EditText) dialogView.findViewById(R.id.card_number);
        cvc = (EditText) dialogView.findViewById(R.id.cvc);
        month = (Spinner) dialogView.findViewById(R.id.expMonth);
        year = (Spinner) dialogView.findViewById(R.id.expYear);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Update", null)
                .setTitle("Update Payment Info")
                .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);

        return dialog;
    }

    @Override
    public void onClick(View view) {

        if (cardNumber != null && cvc != null && !month.getSelectedItem().equals(MONTH_SPINNER_DEFAULT_VALUE)
                && !year.getSelectedItem().equals(YEAR_SPINNER_DEFAULT_VALUE)) {

            String expMonthString = month.getSelectedItem().toString();
            String expYearString = year.getSelectedItem().toString();
            int expMonth = Integer.parseInt(expMonthString);
            int expYear = Integer.parseInt(expYearString);
            String cvcNumber = cvc.getText().toString();
            String number = cardNumber.getText().toString();


            final Card card = new Card(number, expMonth, expYear, cvcNumber);

            if (!card.validateCard()) {
                Toast.makeText(getActivity(), "Could not validate card. Please try again", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Card valid", Toast.LENGTH_SHORT).show();

                try {
                    Stripe stripe = new Stripe(STRIPE_TEST_PUBLISHABLE_KEY);

                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your server

                                    String uid = mAuth.getCurrentUser().getUid();
                                    String email = mAuth.getCurrentUser().getEmail();

                                    Map<String, String> customer = new HashMap<>();
                                    customer.put(FIREBASE_TOKEN_ID, token.getId());
                                    customer.put(FIREBASE_USER_ID, uid);
                                    customer.put(FIREBASE_USER_EMAIL, email);

                                    DatabaseReference mRef = mDatabase.child(FIREBASE_NEW_CUSTOMER).child(FIREBASE_CUSTOMER);
                                    mRef.setValue(customer);

                                }

                                public void onError(Exception error) {
                                    // Show localized error message
                                    Toast.makeText(getContext(),
                                            "Error creating token",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                    );
                } catch (AuthenticationException e) {
                    e.printStackTrace();
                }

            }
        } else {
            Toast.makeText(getActivity(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
        }
    }
}
