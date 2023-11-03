package com.app.videosharingapp.ui.create;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.app.videosharingapp.R;
import com.app.videosharingapp.databinding.FragmentCreateBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateFragment extends Fragment {

    FragmentCreateBinding binding;
    ProgressDialog dialog;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    ExecutorService service;
    Recording recording = null;
    VideoCapture<Recorder> videoCapture = null;
    int cameraFacing = CameraSelector.LENS_FACING_BACK;
    private final int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    public CreateFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentCreateBinding.inflate(inflater,container,false);

        startCamera(cameraFacing);
        dialog=new ProgressDialog(getContext());
        dialog.setTitle("Tiktok");
        dialog.setMessage("Uploading your video.");
        dialog.setCanceledOnTouchOutside(false);
        ActivityResultLauncher<String> activityResultLauncher=registerForActivityResult(new ActivityResultContracts.GetContent(), o -> {
            dialog.show();
            if(o!=null){
                final StorageReference storageReference= firebaseStorage.getReference().child("uploaded_videos").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("post_videos");
                storageReference.putFile(o).addOnSuccessListener(taskSnapshot -> {
                    dialog.dismiss();
                    Toast.makeText(getContext(),"Uploaded",Toast.LENGTH_SHORT).show();
                });
            } else {
                dialog.dismiss();
                Toast.makeText(getContext(),"Video not picked",Toast.LENGTH_SHORT).show();
            }
        });

        binding.capture.setOnClickListener(view -> captureVideo());
        binding.pickVideo.setOnClickListener(view -> activityResultLauncher.launch("video/*"));
        binding.flipCamera.setOnClickListener(view -> {
            if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
                cameraFacing = CameraSelector.LENS_FACING_FRONT;
            } else {
                cameraFacing = CameraSelector.LENS_FACING_BACK;
            }
            startCamera(cameraFacing);
        });
        service = Executors.newSingleThreadExecutor();
        return binding.getRoot();
    }

    public void captureVideo() {
        binding.capture.setImageResource(R.drawable.round_stop_circle_24);
        Recording recording1 = recording;
        if (recording1 != null) {
            recording1.stop();
            recording = null;
            return;
        }
        String name = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault()).format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Tiktok/MyVideos");

        MediaStoreOutputOptions options = new MediaStoreOutputOptions.Builder(requireActivity().getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                .setContentValues(contentValues).build();

        if ((ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(getContext(), "Permission required", Toast.LENGTH_SHORT).show();
            return;
        }
        recording = videoCapture.getOutput().prepareRecording(requireActivity(), options).withAudioEnabled().start(ContextCompat.getMainExecutor(requireActivity()), videoRecordEvent -> {
            if (videoRecordEvent instanceof VideoRecordEvent.Start) {
                binding.capture.setEnabled(true);
            } else if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                if (!((VideoRecordEvent.Finalize) videoRecordEvent).hasError()) {
//                    String msg = "Video capture succeeded: " + ((VideoRecordEvent.Finalize) videoRecordEvent).getOutputResults().getOutputUri();
                    Toast.makeText(getContext(), "Video capture succeeded", Toast.LENGTH_SHORT).show();
                    dialog.show();
                    String videoID = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault()).format(System.currentTimeMillis());
                    final StorageReference storageReference= firebaseStorage.getReference().child("uploaded_videos").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("post_videos").child(videoID);
                    storageReference.putFile(((VideoRecordEvent.Finalize) videoRecordEvent).getOutputResults().getOutputUri()).addOnSuccessListener(taskSnapshot -> {
                        dialog.dismiss();
                        Toast.makeText(getContext(),"Uploaded",Toast.LENGTH_SHORT).show();
                    }).addOnCanceledListener(() -> Toast.makeText(getContext(),"Cancelled by the user",Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(),Toast.LENGTH_SHORT).show());
                } else {
                    if(recording!=null){
                        recording.close();
                        recording = null;
                    }
                    String msg = "Error: " + ((VideoRecordEvent.Finalize) videoRecordEvent).getError();
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
                binding.capture.setImageResource(R.drawable.round_fiber_manual_record_24);
            }
        });
    }

    public void startCamera(int cameraFacing) {
        ListenableFuture<ProcessCameraProvider> processCameraProvider = ProcessCameraProvider.getInstance(requireActivity());
        processCameraProvider.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = processCameraProvider.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());

                Recorder recorder = new Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.HIGHEST)).build();
                videoCapture = VideoCapture.withOutput(recorder);

                cameraProvider.unbindAll();

                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(cameraFacing).build();

                Camera camera = cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, videoCapture);

                binding.toggleFlash.setOnClickListener(view -> toggleFlash(camera));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireActivity()));
    }

    private void toggleFlash(@NonNull Camera camera) {
        if (camera.getCameraInfo().hasFlashUnit()) {
            if (camera.getCameraInfo().getTorchState().getValue() == 0) {
                camera.getCameraControl().enableTorch(true);
                binding.toggleFlash.setImageResource(R.drawable.round_flash_off_24);
            } else {
                camera.getCameraControl().enableTorch(false);
                binding.toggleFlash.setImageResource(R.drawable.round_flash_on_24);
            }
        }
        else {
            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Flash is not available currently", Toast.LENGTH_SHORT).show());
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        service.shutdown();
        binding = null;
    }
}