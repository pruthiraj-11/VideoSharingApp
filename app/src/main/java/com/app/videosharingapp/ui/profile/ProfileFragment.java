package com.app.videosharingapp.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.videosharingapp.Adapters.FragmentAdapter;
import com.app.videosharingapp.Models.UserModel;
import com.app.videosharingapp.R;
import com.app.videosharingapp.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseStorage= FirebaseStorage.getInstance();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.viewPager.setAdapter(new FragmentAdapter(requireActivity().getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserModel userModel=snapshot.getValue(UserModel.class);
                    Picasso.get().load(userModel.getProfilepicURL()).placeholder(R.drawable.ic_action_name).into(binding.userdp);
                    binding.useridprofile.setText(userModel.getUsername());
                    binding.followerscount.setText(userModel.getFollowersCount()+"");
                    binding.followingcount.setText(userModel.getFollowingCount()+"");
                    binding.videoscount.setText(userModel.getVideosCount()+"");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        ActivityResultLauncher<String> activityResultLauncher=registerForActivityResult(new ActivityResultContracts.GetContent(), o -> {
            if(o!=null){
                binding.userdp.setImageURI(o);
                final StorageReference storageReference= firebaseStorage.getReference().child("user_profile_pic").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
                storageReference.putFile(o).addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid()).child("profilepicURL").setValue(uri.toString());
                            Toast.makeText(getContext(),"Profile picture uploaded successfully",Toast.LENGTH_SHORT).show();
                        }
                    });
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(requireContext(),"Profile picture not picked",Toast.LENGTH_SHORT).show();
            }
        });

        ActivityResultLauncher<Intent> launcher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {
            if (o.getResultCode()== RESULT_OK && o.getData()!=null){
                Intent data=o.getData();

                binding.userdp.setImageURI(data.getData());
            } else {
                Toast.makeText(requireContext(),"Profile picture not captured",Toast.LENGTH_SHORT).show();
            }
        });

        binding.profilepicker.setOnClickListener(view -> {
            BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme);
            bottomSheetDialog.setContentView(R.layout.bottomdialog);
            bottomSheetDialog.findViewById(R.id.menu_cancel).setOnClickListener(view1 -> bottomSheetDialog.dismiss());
            bottomSheetDialog.findViewById(R.id.openCamera).setOnClickListener(view13 -> {
                bottomSheetDialog.dismiss();
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    launcher.launch(intent);
                } catch (ActivityNotFoundException e){
                    Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            bottomSheetDialog.findViewById(R.id.openGallery).setOnClickListener(view12 -> {
                bottomSheetDialog.dismiss();
                activityResultLauncher.launch("image/*");
            });
            bottomSheetDialog.show();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}