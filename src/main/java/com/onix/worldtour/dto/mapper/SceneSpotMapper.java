package com.onix.worldtour.dto.mapper;

import com.onix.worldtour.controller.request.SceneSpotRequest;
import com.onix.worldtour.dto.model.SceneSpotDto;
import com.onix.worldtour.model.SceneSpot;
import com.onix.worldtour.util.Util;

public class SceneSpotMapper {
    public static SceneSpot toSceneSpot(SceneSpotRequest sceneSpotRequest) {
        return new SceneSpot()
                .setId(sceneSpotRequest.getId())
                .setName(sceneSpotRequest.getName())
                .setDescription(sceneSpotRequest.getDescription())
                .setPicture(sceneSpotRequest.getPicture())
                .setPanorama(sceneSpotRequest.getPanorama())
                .setPanoramaType(Util.getPanoramaType(sceneSpotRequest.getPanorama()))
                .setVirtual3D(sceneSpotRequest.getVirtual3D())
                .setReview(sceneSpotRequest.getReview());
    }

    public static SceneSpotDto toSceneSpotDto(SceneSpot sceneSpot) {
        return new SceneSpotDto()
                .setId(sceneSpot.getId())
                .setName(sceneSpot.getName())
                .setDescription(sceneSpot.getDescription())
                .setPicture(sceneSpot.getPicture())
                .setPanorama(sceneSpot.getPanorama())
                .setPanoramaType(sceneSpot.getPanoramaType())
                .setVirtual3D(sceneSpot.getVirtual3D())
                .setReview(sceneSpot.getReview());
    }
}
