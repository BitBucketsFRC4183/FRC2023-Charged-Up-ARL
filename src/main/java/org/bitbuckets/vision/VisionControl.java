package org.bitbuckets.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.lib.log.ILoggable;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import java.util.Optional;


public class VisionControl implements Runnable {


    final Transform3d robotToCamera;
    final PhotonCamera photonCamera;
    final AprilTagFieldLayout aprilTagFieldLayout;
    final PhotonPoseEstimator photonPoseEstimator;

    final ILoggable<double[]> loggable;
    final ILoggable<Translation2d[]> loggable2;

    final ILoggable<Pose3d> targetLog;
    private Pose3d targetPose;
    private Pose3d goalPose;

    VisionControl(Transform3d robotToCamera, AprilTagFieldLayout aprilTagFieldLayout, PhotonPoseEstimator photonPoseEstimator, PhotonCamera photonCamera, ILoggable<double[]> loggable, ILoggable<Translation2d[]> loggable2, ILoggable<Pose3d> targetLog) {
        this.robotToCamera = robotToCamera;
        this.aprilTagFieldLayout = aprilTagFieldLayout;
        this.photonPoseEstimator = photonPoseEstimator;
        this.photonCamera = photonCamera;
        this.loggable = loggable;
        this.loggable2 = loggable2;
        this.targetLog = targetLog;
    }

    @Override
    public void run() {
        if (targetPose != null) {
            targetLog.log(goalPose);
        }

    }


    public Optional<Pose3d> estimateRobotPose() {
        Optional<EstimatedRobotPose> result = photonPoseEstimator.update();
        if (result.isEmpty()) return Optional.empty();
        return Optional.of(result.get().estimatedPose);
    }

    public Optional<PhotonCalculationResult> visionPoseEstimator() {
        PhotonPipelineResult result = photonCamera.getLatestResult();
        if (!result.hasTargets()) return Optional.empty();

        PhotonTrackedTarget aprilTagTarget = result.getBestTarget();

        double yaw = aprilTagTarget.getYaw();
        double pitch = aprilTagTarget.getPitch();
        double area = aprilTagTarget.getArea();
        double skew = aprilTagTarget.getSkew();
        Transform3d transformToTag = aprilTagTarget.getBestCameraToTarget();
        double poseX = transformToTag.getX();
        int tagID = aprilTagTarget.getFiducialId();

        //Pose3d robotPose = PhotonUtils.estimateFieldToRobotAprilTag(tagPose, VisionConstants.aprilTags.get(tagID), robotToCamera);
        Optional<EstimatedRobotPose> robotPose3d = photonPoseEstimator.update();

        // load the april tag pose for this tagId
        var aprilTagPose = aprilTagFieldLayout.getTagPose(tagID);
        if (aprilTagPose.isEmpty()) return Optional.empty();

        Pose3d estimatedFieldRobotPose = PhotonUtils.estimateFieldToRobotAprilTag(transformToTag, aprilTagPose.get(), robotToCamera);
        SmartDashboard.putString("robotPOSE", estimatedFieldRobotPose.toString());
        // Transform the robot's pose to find the camera's pose
        var cameraPose = estimatedFieldRobotPose.transformBy(robotToCamera);

        SmartDashboard.putString("tagpose", transformToTag.toString());
        // Trasnform the camera's pose to the target's pose
        targetPose = cameraPose.transformBy(transformToTag);

        // Transform the tag's pose to set our goal
        goalPose = targetPose.transformBy(VisionConstants2.TAG_TO_GOAL);
        // This is new target data, so recalculate the goal
        double range = PhotonUtils.calculateDistanceToTargetMeters(
                VisionConstants2.CAMERA_HEIGHT,
                VisionConstants2.TAG_HEIGHT,
                VisionConstants2.CAMERA_PITCH,
                Units.degreesToRadians(aprilTagTarget.getPitch())
        );

        Translation2d translationToTag = PhotonUtils.estimateCameraToTargetTranslation(
                range, Rotation2d.fromDegrees(-aprilTagTarget.getYaw())
        );

        if (robotPose3d.isEmpty()) return Optional.empty();
        Pose3d currentEstimatedPose3d = robotPose3d.get().estimatedPose;
        Pose2d currentEstimatedPose2d = currentEstimatedPose3d.toPose2d();

        Pose3d tagPossiblePose3d = aprilTagFieldLayout.getTagPose(aprilTagTarget.getFiducialId()).orElseThrow();
        Pose2d tagPossiblePose2d = tagPossiblePose3d.toPose2d();

        Rotation2d targetYaw = PhotonUtils.getYawToPose(currentEstimatedPose2d, goalPose.toPose2d());
        SmartDashboard.putString("targetYaw", targetYaw.toString());
        return Optional.of(new PhotonCalculationResult(estimatedFieldRobotPose, goalPose, translationToTag, targetYaw, targetYaw.getRadians()));

    }
}
