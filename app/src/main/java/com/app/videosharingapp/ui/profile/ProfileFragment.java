package com.app.videosharingapp.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.os.Build;
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
import com.app.videosharingapp.ui.UserFollowersActivity;
import com.app.videosharingapp.ui.UserFollowingActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
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

        firebaseDatabase.getReference().child("Users").child(Objects.requireNonNull(firebaseAuth.getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserModel userModel=snapshot.getValue(UserModel.class);
                    Picasso.get().load(Objects.requireNonNull(userModel).getProfilepicURL()).placeholder(R.drawable.ic_action_name).into(binding.userdp);
                    binding.useridprofile.setText(Objects.requireNonNull(userModel).getUsername());
                    binding.followerscount.setText(String.valueOf(Objects.requireNonNull(userModel).getFollowersCount()));
                    binding.followingcount.setText(String.valueOf(Objects.requireNonNull(userModel).getFollowingCount()));
                    binding.videoscount.setText(String.valueOf(Objects.requireNonNull(userModel).getVideosCount()));
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
                Bitmap bitmap = null;
                ContentResolver contentResolver = requireContext().getContentResolver();
                try {
                    if(Build.VERSION.SDK_INT < 28) {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, o);
                    } else {
                        ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, o);
                        bitmap = ImageDecoder.decodeBitmap(source);
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Failed to process image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final StorageReference storageReference= firebaseStorage.getReference().child("user_profile_pic").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
                storageReference.putFile(o).addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid()).child("profilepicURL").setValue(uri.toString());
                        Toast.makeText(getContext(),"Profile picture uploaded successfully.",Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(requireContext(),"Profile picture not picked.",Toast.LENGTH_SHORT).show();
            }
        });

        ActivityResultLauncher<Intent> launcher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {
            if (o.getResultCode()== RESULT_OK && o.getData()!=null){
                Bundle bundle=o.getData().getExtras();
                Intent intent=o.getData();
                Bitmap bitmap= null;
                if (bundle != null) {
                    bitmap = (Bitmap) bundle.get("data");
                }
                if (bitmap != null) {
                    binding.userdp.setImageBitmap(bitmap);
                    uploadImage(bitmap);
                }
            } else {
                Toast.makeText(requireContext(),"Profile picture not captured.",Toast.LENGTH_SHORT).show();
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
        binding.followerscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserFollowersActivity.class));
            }
        });
        binding.followingcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserFollowingActivity.class));
            }
        });
        return root;
    }

    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        final StorageReference storageReference= firebaseStorage.getReference().child("user_profile_pic").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    firebaseDatabase.getReference().child("Users").child(Objects.requireNonNull(firebaseAuth.getUid())).child("profilepicURL").setValue(uri.toString());
                    Toast.makeText(getContext(),"Profile picture uploaded successfully",Toast.LENGTH_SHORT).show();
                });
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload image.", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}