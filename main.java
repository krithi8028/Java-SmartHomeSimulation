package project;

interface SmartDevice {
    void turnOn();
    void turnOff();
}

package project;

abstract class Device implements SmartDevice {
    private boolean isOn;

    public Device() {
        this.isOn = false;
    }

    @Override
    public void turnOn() {
        isOn = true;
        System.out.println(this.getClass().getSimpleName() + " is turned on.");
    }

    @Override
    public void turnOff() {
        isOn = false;
        System.out.println(this.getClass().getSimpleName() + " is turned off.");
    }

    public boolean isOn() {
        return isOn;
    }
}

package project;

class Light extends Device {
    private int brightness;

    public Light() {
        this.brightness = 0;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
        System.out.println("Light brightness set to " + brightness);
    }

    public int getBrightness() {
        return brightness;
    }
}

package project;

class Thermostat extends Device {
    private int temperature;

    public Thermostat() {
        this.temperature = 20;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
        System.out.println("Thermostat temperature set to " + temperature + " degrees Celsius");
    }

    public int getTemperature() {
        return temperature;
    }
}

package project;

interface CloneableDevice<T extends SmartDevice> extends Cloneable {
    T cloneDevice();
}

package project;

class ClonableLight extends Light implements CloneableDevice<Light> {
    @Override
    public Light cloneDevice() {
        return new ClonableLight();
    }
}

package project;

class ClonableLight extends Light implements CloneableDevice<Light> {
    @Override
    public Light cloneDevice() {
        return new ClonableLight();
    }
}

package project;

class SmartHomeException extends Exception {
    public SmartHomeException(String message) {
        super(message);
    }
}

package project;

import java.util.HashMap;
import java.util.Map;

class SmartHomeDevice<T extends SmartDevice> {
    private Map<String, T> devices;

    public SmartHomeDevice() {
        this.devices = new HashMap<>();
    }

    public void addDevice(String name, T device) {
        devices.put(name, device);
    }

    public T getDevice(String name) {
        return devices.get(name);
    }
}

package project;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmartHome {
    public static void main(String[] args) {
        SmartHomeDevice<SmartDevice> smartHomeDevice = new SmartHomeDevice<>();

        Light livingRoomLight = new Light();
        Thermostat livingRoomThermostat = new Thermostat();
        ClonableLight clonedLight = new ClonableLight();

        smartHomeDevice.addDevice("LivingRoomLight", livingRoomLight);
        smartHomeDevice.addDevice("LivingRoomThermostat", livingRoomThermostat);
        smartHomeDevice.addDevice("ClonedLight", clonedLight);

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        try {
            executorService.execute(() -> {
                SmartDevice device = smartHomeDevice.getDevice("LivingRoomLight");
                if (device != null) {
                    device.turnOn();
                    ((Light) device).setBrightness(75);
                    device.turnOff();
                }
            });

            executorService.execute(() -> {
                SmartDevice device = smartHomeDevice.getDevice("LivingRoomThermostat");
                if (device != null) {
                    device.turnOn();
                    ((Thermostat) device).setTemperature(22);
                    device.turnOff();
                }
            });

            executorService.execute(() -> {
                SmartDevice device = smartHomeDevice.getDevice("ClonedLight");
                if (device instanceof CloneableDevice) {
                    CloneableDevice<Light> cloneableDevice = (CloneableDevice<Light>) device;
                    SmartDevice clonedDevice = cloneableDevice.cloneDevice();
                    smartHomeDevice.addDevice("ClonedLightCopy", (Light) clonedDevice);
                }
            });
            throw new SmartHomeException("Something went wrong!");
            
        } catch (SmartHomeException e) {
            System.out.println("Error in smart home: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }
}
