package com.example.swainstha.dronefly;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by swainstha on 5/28/18.
 */

public class Geo {

    private double lat;
    private double lon;
    private double distance;
    private double radius = 6371000;

    public Geo(LatLng latLng, Integer distance) {
        this.lat = latLng.latitude;
        this.lon = latLng.longitude;
        this. distance = distance;
    }

    public ArrayList<Double> calculate() {
        ArrayList<Double> data = new ArrayList<>();
        List<Double> list = Arrays.asList(0.0,90.0,180.0,270.0);
        for(double angle:list) {

            double a = 6378137,
                    b = 6356752.3142,
                    f = 1 / 298.257223563, // WGS-84 ellipsiod
                    s = distance,
                    alpha1 = deg2rad(angle),
                    sinAlpha1 = Math.sin(alpha1),
                    cosAlpha1 = Math.cos(alpha1),
                    tanU1 = (1 - f) * Math.tan(deg2rad(lat)),
                    cosU1 = 1 / Math.sqrt((1 + tanU1 * tanU1)), sinU1 = tanU1 * cosU1,
                    sigma1 = Math.atan2(tanU1, cosAlpha1),
                    sinAlpha = cosU1 * sinAlpha1,
                    cosSqAlpha = 1 - sinAlpha * sinAlpha,
                    uSq = cosSqAlpha * (a * a - b * b) / (b * b),
                    A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq))),
                    B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq))),
                    sigma = s / (b * A),
                    sigmaP = 2 * Math.PI;

            double sinSigma = 0,cosSigma = 0, cos2SigmaM = 0,deltaSigma;
            while (Math.abs(sigma - sigmaP) > 1e-12) {
                cos2SigmaM = Math.cos(2 * sigma1 + sigma);
                sinSigma = Math.sin(sigma);
                cosSigma = Math.cos(sigma);
                deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
                sigmaP = sigma;
                sigma = s / (b * A) + deltaSigma;
            }

            double tmp = sinU1 * sinSigma - cosU1 * cosSigma * cosAlpha1,
                    lat2 = Math.atan2(sinU1 * cosSigma + cosU1 * sinSigma * cosAlpha1, (1 - f) * Math.sqrt(sinAlpha * sinAlpha + tmp * tmp)),
                    lambda = Math.atan2(sinSigma * sinAlpha1, cosU1 * cosSigma - sinU1 * sinSigma * cosAlpha1),
                    C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha)),
                    L = lambda - (1 - C) * f * sinAlpha * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM))),
                    revAz = Math.atan2(sinAlpha, -tmp); // final bearing

            double newLat = rad2deg(lat2);
            double newLon = lon + rad2deg(L);
            data.add(newLat);
            data.add(newLon);
        }
        return data;
    }

    private static double deg2rad(double deg) {
        return ((deg * Math.PI) / 180.0);

    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

}
