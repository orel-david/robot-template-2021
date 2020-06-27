package frc.robot.subsystems.base.common.motors;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.utilities.TalonConfiguration;

/**
 * A builder for Talon SRX motor controllers.
 *
 * @author Barel
 * @version 1.0
 * @since 2020-off
 */
public class TalonBuilder {
    private final TalonSRX motor;

    /**
     * Create a builder for the talons.
     *
     * @param port         the port of the talon.
     * @param inverted     whether the talon is inverted.
     * @param openLoopRamp Configures the open-loop ramp rate.
     */
    public TalonBuilder(int port, boolean inverted, double openLoopRamp) {
        motor = new TalonSRX(port);
        motor.configFactoryDefault();
        motor.setInverted(inverted);
        motor.configOpenloopRamp(openLoopRamp);
        motor.setNeutralMode(NeutralMode.Coast);
        motor.enableVoltageCompensation(true);
        motor.configVoltageCompSaturation(12.);
    }

    /**
     * config set of the motors.
     *
     * @param configurations the configuration.
     * @param talons         the talons to configure.
     */
    public static void configMotors(TalonConfiguration configurations, TalonSRX... talons) {
        for (TalonSRX talonSRX : talons) {
            talonSRX.configAllSettings(configurations.motorConfigs);
            talonSRX.configVoltageCompSaturation(configurations.getVoltageCompensationSaturation());
            talonSRX.setNeutralMode(configurations.getNeutralMode());
            talonSRX.enableVoltageCompensation(configurations.isEnableVoltageCompensation());
            talonSRX.config_kP(0, configurations.getPidSet()[0]);
            talonSRX.config_kI(0, configurations.getPidSet()[1]);
            talonSRX.config_kD(0, configurations.getPidSet()[2]);
            talonSRX.config_kF(0, configurations.getPidSet()[3]);
        }
    }

    /**
     * Set PID to talons.
     *
     * @param kp     proportional.
     * @param ki     integral.
     * @param kd     derivative.
     * @param kf     feed-forward.
     * @param talons talons to configure.
     */
    public static void configPID(double kp, double ki, double kd, double kf, TalonSRX... talons) {
        for (TalonSRX motor : talons) {
            motor.config_kP(0, kp, 10);
            motor.config_kI(0, ki, 10);
            motor.config_kD(0, kd, 10);
            motor.config_kF(0, kf, 10);
        }
    }

    /**
     * @param mode the new mode.
     * @return a reference to this object.
     */
    public TalonBuilder overrideNeuralMode(NeutralMode mode) {
        motor.setNeutralMode(mode);
        return this;
    }

    /**
     * @param position    sensor position.
     * @param sensorPhase the phase of the sensor.
     * @return a reference to this object.
     * @see #addSensor(boolean)
     */
    public TalonBuilder addSensor(int position, boolean sensorPhase) {
        motor.setSelectedSensorPosition(position);
        motor.setSensorPhase(sensorPhase);

        return this;
    }

    /**
     * @param sensorPhase the phase of the sensor.
     * @return a reference to this object.
     * @see #addSensor(int, boolean)
     */
    public TalonBuilder addSensor(boolean sensorPhase) {
        return addSensor(0, sensorPhase);
    }

    /**
     * config the PID for the talon.
     *
     * @param slot    the slot of the pid.
     * @param kp      proportional.
     * @param ki      integral.
     * @param kd      derivative.
     * @param kf      feed-forward.
     * @param timeout the delta time between each change to the PID.
     * @return a reference to this object.
     * @see #addPID(int, double, double, double, double)
     * @see #addPID(double, double, double, double)
     */
    public TalonBuilder addPID(int slot, double kp, double ki, double kd, double kf, int timeout) {
        motor.config_kP(slot, kp, timeout);
        motor.config_kI(slot, ki, timeout);
        motor.config_kD(slot, kd, timeout);
        motor.config_kF(slot, kf, timeout);

        return this;
    }

    /**
     * config the PID for the talon.
     *
     * @param slot the slot of the pid.
     * @param kp   proportional.
     * @param ki   integral.
     * @param kd   derivative.
     * @param kf   feed-forward.
     * @return a reference to this object.
     * @see #addPID(int, double, double, double, double, int)
     * @see #addPID(double, double, double, double)
     */
    public TalonBuilder addPID(int slot, double kp, double ki, double kd, double kf) {
        return addPID(slot, kp, ki, kd, kf, 10);
    }

    /**
     * config the PID for the talon.
     *
     * @param kp proportional.
     * @param ki integral.
     * @param kd derivative.
     * @param kf feed-forward.
     * @return a reference to this object.
     * @see #addPID(int, double, double, double, double)
     * @see #addPID(int, double, double, double, double, int)
     */
    public TalonBuilder addPID(double kp, double ki, double kd, double kf) {
        return addPID(0, kp, ki, kd, kf);
    }

    /**
     * config the soft limits and cruise velocity.
     *
     * @param sofLimitEnabled whether the soft limit is enabled.
     * @param softThreshold   the threshold.
     * @param cruiseVelocity  cruise velocity.
     * @return a reference to this object.
     */
    public TalonBuilder configPhysics(boolean sofLimitEnabled, int softThreshold, int acceleration, int cruiseVelocity) {
        motor.configForwardSoftLimitEnable(sofLimitEnabled);
        motor.configReverseSoftLimitEnable(sofLimitEnabled);
        motor.configForwardSoftLimitThreshold(softThreshold);
        motor.configReverseSoftLimitThreshold(softThreshold);

        motor.configMotionAcceleration(acceleration);
        motor.configMotionCruiseVelocity(cruiseVelocity);
        return this;
    }

    /**
     * config the current limit.
     *
     * @param enabled       whether the limit is enabled.
     * @param supply        current limit to supply.
     * @param threshold     the threshold.
     * @param thresholdTime the threshold time.
     * @return a reference to this object.
     */
    public TalonBuilder configSupplyCurrentLimit(boolean enabled, double supply, double threshold, int thresholdTime) {
        motor.configSupplyCurrentLimit(
                new SupplyCurrentLimitConfiguration(enabled, supply, threshold, thresholdTime)
        );

        return this;
    }

    /**
     * config other limits.
     *
     * @param continuous          the continuous limit.
     * @param peakCurrent         the peak limit.
     * @param peakCurrentDuration the peak duration.
     * @return a reference to this object.
     */
    public TalonBuilder configLimits(int continuous, int peakCurrent, int peakCurrentDuration) {
        motor.configContinuousCurrentLimit(continuous);
        motor.configPeakCurrentLimit(peakCurrent);
        motor.configPeakCurrentDuration(peakCurrentDuration);
        return this;
    }

    /**
     * @param master the talon to follow.
     * @return a reference to this object.
     */
    public TalonBuilder follow(TalonFX master) {
        motor.follow(master);
        return this;
    }

    /**
     * Create a talon object.
     *
     * @return new Talon.
     */
    public TalonSRX build() {
        return motor;
    }
}
