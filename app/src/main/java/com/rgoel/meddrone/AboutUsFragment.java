package com.rgoel.meddrone;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

public class AboutUsFragment extends Fragment {

    public AboutUsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);

        VideoView simulationVideo = view.findViewById(R.id.simulationVideo);
        String videoPath = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.simulation;
        Uri uri = Uri.parse(videoPath);
        simulationVideo.setVideoURI(uri);

        MediaController mediaController = new MediaController(getActivity());
        simulationVideo.setMediaController(mediaController);
        mediaController.setAnchorView(simulationVideo);

        view.findViewById(R.id.buttonPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simulationVideo.start();
            }
        });

        return view;
    }
}