package frc.robot.utilities;

import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.subsystems.base.common.Constant;
import frc.robot.subsystems.base.common.RangeConstant;
import org.apache.commons.lang.math.DoubleRange;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.LongRange;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;


public class Utils {

    /**
     * set the value of an entry in the network table
     *
     * @param entry the network table entry's name
     * @param value the value of the entry
     */
    public static void setValue(String table, String entry, Object value) {
        NetworkTableInstance.getDefault().getTable(table).getEntry(entry).setValue(value);
    }

    /**
     * set the value of an entry in a known network table
     *
     * @param entry the network table entry's name
     * @param value the value of the entry
     */
    public static void setValue(NetworkTableEntry entry, double value) {
        entry.setValue(value);
    }


    /**
     * recreates the results of Math.floorMod() for Double type variables.
     * The result is the unsigned remainder of the mod method.
     *
     * @param value the numerator
     * @param mod   the denominator
     * @return the remainder of the division
     */
    public static double floorMod(double value, double mod) {
        value %= mod;
        value += mod;
        value %= mod;
        return value;
    }

    /**
     * Replaces fields between constants classes.
     *
     * @param class1 Original constants class
     */
    public static void replaceFields(Class<?> class1, Class<?> class2) {
        //Loop and replace all fields
        for (Field f : class2.getDeclaredFields()) {
            for (Field f2 : class1.getDeclaredFields()) {
                //Loop and replace all fields
                if (f2.getName().equals(f.getName())) { // If the name is equal perform replacement

                    f2.setAccessible(true);
                    f.setAccessible(true);
                    try {
                        Field modifiersField = Field.class.getDeclaredField("modifiers");
                        modifiersField.setAccessible(true);
                        modifiersField.setInt(f2, f2.getModifiers() & ~Modifier.FINAL);
                        f2.set(null, f.get(null));
                    } catch (IllegalAccessException | NoSuchFieldException e) { // Catch relevant exceptions
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void replaceFieldUsingAnnotation(Class<?> clazz) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(Constant.class)) {
                f.setAccessible(true);
                Constant constant = f.getAnnotation(Constant.class);
                try {
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);

                    changeField(f, constant);
                } catch (IllegalAccessException | NoSuchFieldException e) { // Catch relevant exceptions
                    e.printStackTrace();
                }

            } else if (f.isAnnotationPresent(RangeConstant.class)) {
                f.setAccessible(true);
                RangeConstant constant = f.getAnnotation(RangeConstant.class);
                try {
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);

                    changeRangeField(f, constant);
                } catch (IllegalAccessException | NoSuchFieldException e) { // Catch relevant exceptions
                    e.printStackTrace();
                }

            }
        }
    }

    private static void changeRangeField(Field field, RangeConstant constant) throws IllegalAccessException {
        if (DoubleRange.class.equals(field.getType()))
            field.set(null, new DoubleRange(constant.minDouble(), constant.maxDouble()));
        else if (IntRange.class.equals(field.getType()))
            field.set(null, new IntRange(constant.minInt(), constant.maxInt()));
        else if (LongRange.class.equals(field.getType()))
            field.set(null, new LongRange(constant.minLong(), constant.maxLong()));
        else
            throw new IllegalArgumentException("The specified type isn't supported: " + field.getType().toString());
    }

    private static void changeField(Field field, Constant constant) throws IllegalAccessException {
        if (double.class.equals(field.getType()))
            field.setDouble(null, constant.doubleVal());
        else if (int.class.equals(field.getType()))
            field.setInt(null, constant.intVal());
        else if (long.class.equals(field.getType()))
            field.setLong(null, constant.longVal());
        else
            throw new IllegalArgumentException("The specified type isn't supported: " + field.getType().toString());
    }

    public static void swapConstants(Class<?> original) {
//        Utils.replaceFields(original, B); // Replace outer constants
        replaceFieldUsingAnnotation(original);
        for (Class<?> aClass : original.getDeclaredClasses()) { // Loop constants classes
            replaceFieldUsingAnnotation(aClass);

            // Find the class in B Constants
//            Optional<Class<?>> bClass = Arrays.stream(B.getDeclaredClasses()).filter(c -> c.getSimpleName().equals(aClass.getSimpleName())).findAny();
//            if (bClass.isEmpty()) continue; // Class isn't present
//            Utils.replaceFields(aClass, bClass.get());
        }
    }

    /**
     * return whether or not the array contains a specified number.
     *
     * @param a   the array to check.
     * @param val the value to check.
     * @return whether or not the array contains the specified number.
     */
    public static boolean arrayContains(int[] a, int val) {
        for (int var : a) {
            if (var == val)
                return false;
        }
        return true;
    }

    public static double clamp(double val, double min, double max) {
        return Math.min(min, Math.max(val, max));
    }

    public static int clamp(int val, int min, int max) {
        return Math.min(min, Math.max(val, max));
    }
}
