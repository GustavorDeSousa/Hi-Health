package br.com.thecharles.hihealth.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.activity.ChatActivity;
import br.com.thecharles.hihealth.activity.LoginActivity;
import br.com.thecharles.hihealth.adapter.ContactsAdapter;
import br.com.thecharles.hihealth.config.SettingsFirebase;
import br.com.thecharles.hihealth.helper.RecyclerItemClickListener;
import br.com.thecharles.hihealth.model.User;

public class ContactsFragment extends Fragment {

    private static final String TAG = "Contacts";
    private Switch swtich;

//    private String userID;
    private RecyclerView recyclerViewListContacts;
    private ContactsAdapter adapter;
    private ArrayList<User> listContacts = new ArrayList<>();
//    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference();

    DatabaseReference firebaseRef = SettingsFirebase.getFirebaseDatabase();
    DatabaseReference firebaseRefDebug = firebaseRef.child("debug");

    private ValueEventListener valueEventListenerContacts;
    private FirebaseUser userCurrent;
//    FirebaseUser userCurrent = firebaseAuth.getCurrentUser();

    public ContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_contact);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();



                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

                //Pegando o dialog
                View mView = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);

                //Buttons do Dialog
                ImageView mSearch =  mView.findViewById(R.id.btnSearch);
                final EditText editText  = mView.findViewById(R.id.edtAddContact);


                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                mSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userAdd = editText.getText().toString();

                        if( !userAdd.isEmpty() ) {//verifica e-mail
                            dialog.dismiss();
                            findUser();
                        } else {
                            Toast.makeText(getActivity(),
                                    "Preencha o email!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        //Configurações iniciais
//        FirebaseUser userCurrent = firebaseAuth.getCurrentUser();
//        userID = user.getUid();
        recyclerViewListContacts = view.findViewById(R.id.rvListContacts);
        firebaseRefDebug = firebaseRefDebug.child("users");
        userCurrent = FirebaseAuth.getInstance().getCurrentUser();
        swtich = view.findViewById(R.id.sAlert);

        //TODO configurar adapter
        adapter = new ContactsAdapter(listContacts, getActivity(), swtich);

        //TODO configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListContacts.setLayoutManager(layoutManager);
        recyclerViewListContacts.setHasFixedSize(true);
        recyclerViewListContacts.setAdapter(adapter);

        //TODO configurar evendo de clique no recyclerview
        recyclerViewListContacts.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewListContacts,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                User selectedUser = listContacts.get(position);

                                Intent intent = new Intent(getActivity(), ChatActivity.class);
                                intent.putExtra("chatContact", selectedUser);
                                Log.d(TAG, "Chat: " + selectedUser.getName() +
                                        " - " + selectedUser.getEmail() + "\n");
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                ));

        return view;
    }

    private void findUser() {
        Toast.makeText(getActivity(),
                "Pesquisando usário!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        getContacts();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRefDebug.removeEventListener(valueEventListenerContacts);
    }

    public void getContacts() {
       valueEventListenerContacts =  firebaseRefDebug.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren())  {
                    User user = dados.child("registered").getValue(User.class);

                    String emailCurent = userCurrent.getEmail();
                    if (!emailCurent.equals( user.getEmail())) {
                        listContacts.add(user);
                    }

                    Log.d(TAG, "Contatos: " + user.getName() + " - " + user.getEmail() + "\n");
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }
}
