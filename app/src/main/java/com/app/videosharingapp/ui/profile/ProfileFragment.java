package com.app.videosharingapp.ui.profile;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private LruCache<String, Bitmap> memoryCache;
    private final String USERPROFILEKEY="userProfilePic";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseStorage= FirebaseStorage.getInstance();
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.viewPager.setAdapter(new FragmentAdapter(requireActivity().getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        sharedPreferences=requireContext().getSharedPreferences("appCache", MODE_PRIVATE);
        boolean flag=sharedPreferences.getBoolean("isProfileCached",false);
        if (flag){
            loadBitmap(USERPROFILEKEY);
        }
        firebaseDatabase.getReference().child("Users").child(Objects.requireNonNull(firebaseAuth.getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserModel userModel=snapshot.getValue(UserModel.class);
                    if (!flag){
                        Picasso.get().load(Objects.requireNonNull(userModel).getProfilepicURL()).placeholder(R.drawable.ic_action_name).into(binding.userdp);
                    }
                    binding.useridprofile.setText(Objects.requireNonNull(userModel).getUsername());
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
                Bitmap finalBitmap = bitmap;
                storageReference.putFile(o).addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid()).child("profilepicURL").setValue(uri.toString());
                        addBitmapToMemoryCache(USERPROFILEKEY, finalBitmap);
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
                    addBitmapToMemoryCache(USERPROFILEKEY,bitmap);
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

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key,bitmap);
            editor=requireContext().getSharedPreferences("appCache",MODE_PRIVATE).edit();
            editor.putBoolean("isProfileCached",true);
            editor.apply();
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }
    public void loadBitmap(String imageKey) {
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            binding.userdp.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}