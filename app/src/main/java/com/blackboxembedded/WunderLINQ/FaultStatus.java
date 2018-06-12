package com.blackboxembedded.WunderLINQ;

import android.content.Context;
import android.content.ContextWrapper;

import java.util.ArrayList;

/**
 * Created by keithconger on 12/29/17.
 */

public class FaultStatus extends ContextWrapper {

    // Debug faults
    private static boolean uartOverflowActive = false;
    private static String uartOverflowDesc = "";

    private static boolean uartFrameActive = false;
    private static String uartFrameDesc = "";

    private static boolean uartLogicActive = false;
    private static String uartLogicDesc = "";

    // Motorcycle faults
    private static boolean absSelfDiagActive = false;
    private static String absSelfDiagDesc = "";

    private static boolean absDeactivatedActive = false;
    private static String absDeactivatedDesc = "";

    private static boolean absErrorActive = false;
    private static String absErrorDesc = "";

    private static boolean ascSelfDiagActive = false;
    private static String ascSelfDiagDesc = "";

    private static boolean ascInterventionActive = false;
    private static String ascInterventionDesc = "";

    private static boolean ascDeactivatedActive = false;
    private static String ascDeactivatedDesc = "";

    private static boolean ascErrorActive = false;
    private static String ascErrorDesc = "";

    private static boolean fuelFaultActive = false;
    private static String fuelFaultDesc = "";

    private static boolean frontTirePressureWarningActive = false;
    private static String frontTirePressureWarningDesc = "";

    private static boolean rearTirePressureWarningActive = false;
    private static String rearTirePressureWarningDesc = "";

    private static boolean frontTirePressureCriticalNotificationActive = false;
    private static boolean frontTirePressureCriticalActive = false;
    private static String frontTirePressureCriticalDesc = "";

    private static boolean rearTirePressureCriticalNotificationActive = false;
    private static boolean rearTirePressureCriticalActive = false;
    private static String rearTirePressureCriticalDesc = "";

    private static boolean addFrontLightOneActive = false;
    private static String addFrontLightOneDesc = "";

    private static boolean addFrontLightTwoActive = false;
    private static String addFrontLightTwoDesc = "";

    private static boolean daytimeRunningActive = false;
    private static String daytimeRunningDesc = "";

    private static boolean frontLeftSignalActive = false;
    private static String frontLeftSignalDesc = "";

    private static boolean frontRightSignalActive = false;
    private static String frontRightSignalDesc = "";

    private static boolean rearLeftSignalActive = false;
    private static String rearLeftSignalDesc = "";

    private static boolean rearRightSignalActive = false;
    private static String rearRightSignalDesc = "";

    private static boolean frontParkingLightOneActive = false;
    private static String frontParkingLightOneDesc = "";

    private static boolean frontParkingLightTwoActive = false;
    private static String frontParkingLightTwoDesc = "";

    private static boolean lowBeamActive = false;
    private static String lowBeamDesc = "";

    private static boolean highBeamActive = false;
    private static String highBeamDesc = "";

    private static boolean rearLightActive = false;
    private static String rearLightDesc = "";

    private static boolean brakeLightActive = false;
    private static String brakeLightDesc = "";

    private static boolean licenseLightActive = false;
    private static String licenseLightDesc = "";

    private static boolean rearFogLightActive = false;
    private static String rearFogLightDesc = "";

    private static boolean addDippedLightActive = false;
    private static String addDippedLightDesc = "";

    private static boolean addBrakeLightActive = false;
    private static String addBrakeLightDesc = "";

    private static boolean frontLampOneLightActive = false;
    private static String frontLampOneLightDesc = "";

    private static boolean frontLampTwoLightActive = false;
    private static String frontLampTwoLightDesc = "";

    private static boolean iceWarningActive = false;
    private static String iceWarningDesc = "";

    private static boolean generalFlashingYellowActive = false;
    private static String generalFlashingYellowDesc = "";

    private static boolean generalShowsYellowActive = false;
    private static String generalShowsYellowDesc = "";

    private static boolean generalFlashingRedNotificationActive = false;
    private static boolean generalFlashingRedActive = false;
    private static String generalFlashingRedDesc = "";

    private static boolean generalShowsRedNotificationActive = false;
    private static boolean generalShowsRedActive = false;
    private static String generalShowsRedDesc = "";

    private static boolean oilLowActive = false;
    private static String oilLowDesc = "";

    public FaultStatus(Context base) {
        super(base);
        // Debug Faults
        uartOverflowDesc = MainActivity.getContext().getResources().getString(R.string.fault_UARTOF);
        uartFrameDesc = MainActivity.getContext().getResources().getString(R.string.fault_UARTFF);
        uartLogicDesc = MainActivity.getContext().getResources().getString(R.string.fault_UARTLF);

        // Motorcycle faults
        absSelfDiagDesc = MainActivity.getContext().getResources().getString(R.string.fault_ABSSLF);
        absDeactivatedDesc = MainActivity.getContext().getResources().getString(R.string.fault_ABSDAC);
        absErrorDesc = MainActivity.getContext().getResources().getString(R.string.fault_ABSERR);
        ascSelfDiagDesc = MainActivity.getContext().getResources().getString(R.string.fault_ASCSLF);
        ascInterventionDesc = MainActivity.getContext().getResources().getString(R.string.fault_ASCINT);
        ascDeactivatedDesc = MainActivity.getContext().getResources().getString(R.string.fault_ASCDAC);
        ascErrorDesc = MainActivity.getContext().getResources().getString(R.string.fault_ASCERR);
        fuelFaultDesc = MainActivity.getContext().getResources().getString(R.string.fault_FUELF);
        frontTirePressureWarningDesc = MainActivity.getContext().getResources().getString(R.string.fault_TIREFWF);
        rearTirePressureWarningDesc = MainActivity.getContext().getResources().getString(R.string.fault_TIRERWF);
        frontTirePressureCriticalDesc = MainActivity.getContext().getResources().getString(R.string.fault_TIREFCF);
        rearTirePressureCriticalDesc = MainActivity.getContext().getResources().getString(R.string.fault_TIRERCF);
        addFrontLightOneDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPAFL1);
        addFrontLightTwoDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPAFL2);
        daytimeRunningDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPDAY);
        frontLeftSignalDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPFLI);
        frontRightSignalDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPFRI);
        rearLeftSignalDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPRLI);
        rearRightSignalDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPRRI);
        frontParkingLightOneDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPFPK1);
        frontParkingLightTwoDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPFPK2);
        lowBeamDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPLOW);
        highBeamDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPHI);
        rearLightDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPREAR);
        brakeLightDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPBRK);
        licenseLightDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPLIC);
        rearFogLightDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPRFOG);
        addDippedLightDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPADDD);
        addBrakeLightDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPADDB);
        frontLampOneLightDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPFL1);
        frontLampTwoLightDesc = MainActivity.getContext().getResources().getString(R.string.fault_LAMPFL2);
        iceWarningDesc = MainActivity.getContext().getResources().getString(R.string.fault_ICEWARN);
        generalFlashingYellowDesc = MainActivity.getContext().getResources().getString(R.string.fault_GENWARNFSYLW);
        generalShowsYellowDesc = MainActivity.getContext().getResources().getString(R.string.fault_GENWARNSHYLW);
        generalFlashingRedDesc = MainActivity.getContext().getResources().getString(R.string.fault_GENWARNFSRED);
        generalShowsRedDesc = MainActivity.getContext().getResources().getString(R.string.fault_GENWARNSHRED);
        oilLowDesc = MainActivity.getContext().getResources().getString(R.string.fault_OILLOW);

    }

    public static ArrayList<String> getallActiveDesc() {
        ArrayList<String> allActiveDesc = new ArrayList<String>();
        // Debug faults
        if(uartOverflowActive){
            allActiveDesc.add(uartOverflowDesc);
        }
        if(uartFrameActive){
            allActiveDesc.add(uartFrameDesc);
        }
        if(uartLogicActive){
            allActiveDesc.add(uartLogicDesc);
        }

        // Motorcycle faults
        if(absSelfDiagActive){
            allActiveDesc.add(absSelfDiagDesc);
        }
        if(absDeactivatedActive){
            allActiveDesc.add(absDeactivatedDesc);
        }
        if(absErrorActive){
            allActiveDesc.add(absErrorDesc);
        }
        if(ascSelfDiagActive){
            allActiveDesc.add(ascSelfDiagDesc);
        }
        if(ascInterventionActive){
            allActiveDesc.add(ascInterventionDesc);
        }
        if(ascDeactivatedActive){
            allActiveDesc.add(ascDeactivatedDesc);
        }
        if(ascErrorActive){
            allActiveDesc.add(ascErrorDesc);
        }
        if(fuelFaultActive){
            allActiveDesc.add(fuelFaultDesc);
        }
        if(frontTirePressureWarningActive){
            allActiveDesc.add(frontTirePressureWarningDesc);
        }
        if(rearTirePressureWarningActive){
            allActiveDesc.add(rearTirePressureWarningDesc);
        }
        if(frontTirePressureCriticalActive){
            allActiveDesc.add(frontTirePressureCriticalDesc);
        }
        if(rearTirePressureCriticalActive){
            allActiveDesc.add(rearTirePressureCriticalDesc);
        }
        if(addFrontLightOneActive){
            allActiveDesc.add(addFrontLightOneDesc);
        }
        if(addFrontLightTwoActive){
            allActiveDesc.add(addFrontLightTwoDesc);
        }
        if(daytimeRunningActive){
            allActiveDesc.add(daytimeRunningDesc);
        }
        if(frontLeftSignalActive){
            allActiveDesc.add(frontLeftSignalDesc);
        }
        if(frontRightSignalActive){
            allActiveDesc.add(frontRightSignalDesc);
        }
        if(rearLeftSignalActive){
            allActiveDesc.add(rearLeftSignalDesc);
        }
        if(rearRightSignalActive){
            allActiveDesc.add(rearRightSignalDesc);
        }
        if(frontParkingLightOneActive){
            allActiveDesc.add(frontParkingLightOneDesc);
        }
        if(frontParkingLightTwoActive){
            allActiveDesc.add(frontParkingLightTwoDesc);
        }
        if(lowBeamActive){
            allActiveDesc.add(lowBeamDesc);
        }
        if(highBeamActive){
            allActiveDesc.add(highBeamDesc);
        }
        if(rearLightActive){
            allActiveDesc.add(rearLightDesc);
        }
        if(brakeLightActive){
            allActiveDesc.add(brakeLightDesc);
        }
        if(licenseLightActive){
            allActiveDesc.add(licenseLightDesc);
        }
        if(rearFogLightActive){
            allActiveDesc.add(rearFogLightDesc);
        }
        if(addDippedLightActive){
            allActiveDesc.add(addDippedLightDesc);
        }
        if(addBrakeLightActive){
            allActiveDesc.add(addBrakeLightDesc);
        }
        if(frontLampOneLightActive){
            allActiveDesc.add(frontLampOneLightDesc);
        }
        if(frontLampTwoLightActive){
            allActiveDesc.add(frontLampTwoLightDesc);
        }
        if(iceWarningActive){
            allActiveDesc.add(iceWarningDesc);
        }
        if(generalFlashingYellowActive){
            allActiveDesc.add(generalFlashingYellowDesc);
        }
        if(generalShowsYellowActive){
            allActiveDesc.add(generalShowsYellowDesc);
        }
        if(generalFlashingRedActive){
            allActiveDesc.add(generalFlashingRedDesc);
        }
        if(generalShowsRedActive){
            allActiveDesc.add(generalShowsRedDesc);
        }
        if(oilLowActive){
            allActiveDesc.add(oilLowDesc);
        }

        return allActiveDesc;
    }

    // Debug faults
    public static void setUartOverflowActive(boolean uartOverflowActive){
        FaultStatus.uartOverflowActive = uartOverflowActive;
    }
    public static boolean getUartOverflowActive() {
        return uartOverflowActive;
    }
    public static String getUartOverflowDesc() {
        return uartOverflowDesc;
    }

    public static void setUartFrameActive(boolean uartFrameActive){
        FaultStatus.uartFrameActive = uartFrameActive;
    }
    public static boolean getUartFrameActive() {
        return uartFrameActive;
    }
    public static String getUartFrameDesc() {
        return uartFrameDesc;
    }

    public static void setUartLogicActive(boolean uartLogicActive){
        FaultStatus.uartLogicActive = uartLogicActive;
    }
    public static boolean getUartLogicActive() {
        return uartLogicActive;
    }
    public static String getUartLogicDesc() {
        return uartLogicDesc;
    }


    // Motorcycle faults
    public static void setAbsSelfDiagActive(boolean absSelfDiagActive){
        FaultStatus.absSelfDiagActive = absSelfDiagActive;
    }
    public static boolean getabsSelfDiagActive() {
        return absSelfDiagActive;
    }
    public static String getabsSelfDiagDesc() {
        return absSelfDiagDesc;
    }

    public static void setAbsDeactivatedActive(boolean absDeactivatedActive){
        FaultStatus.ascDeactivatedActive = absDeactivatedActive;
    }
    public static boolean getabsDeactivatedActive() {
        return absDeactivatedActive;
    }
    public static String getabsDeactivatedDesc() {
        return absDeactivatedDesc;
    }

    public static void setabsErrorActive(boolean absErrorActive){
        FaultStatus.absErrorActive = absErrorActive;
    }
    public static boolean getabsErrorActive() {
        return absErrorActive;
    }
    public static String getabsErrorDesc() {
        return absErrorDesc;
    }

    public static void setAscSelfDiagActive(boolean ascSelfDiagActive){
        FaultStatus.ascSelfDiagActive = ascSelfDiagActive;
    }
    public static boolean getascSelfDiagActive() {
        return ascSelfDiagActive;
    }
    public static String getascSelfDiagDesc() {
        return ascSelfDiagDesc;
    }

    public static void setAscInterventionActive(boolean ascInterventionActive){
        FaultStatus.ascInterventionActive = ascInterventionActive;
    }
    public static boolean getascInterventionActive() {
        return ascInterventionActive;
    }
    public static String getascInterventionDesc() {
        return ascInterventionDesc;
    }

    public static void setAscDeactivatedActive(boolean ascDeactivatedActive){
        FaultStatus.ascDeactivatedActive = ascDeactivatedActive;
    }
    public static boolean getascDeactivatedActive() {
        return ascDeactivatedActive;
    }
    public static String getascDeactivatedDesc() {
        return ascDeactivatedDesc;
    }

    public static void setascErrorActive(boolean ascErrorActive){
        FaultStatus.ascErrorActive = ascErrorActive;
    }
    public static boolean getascErrorActive() {
        return ascErrorActive;
    }
    public static String getascErrorDesc() {
        return ascErrorDesc;
    }

    public static void setfuelFaultActive(boolean fuelFaultActive){
        FaultStatus.fuelFaultActive = fuelFaultActive;
    }
    public static boolean getfuelFaultActive() {
        return fuelFaultActive;
    }
    public static String getfuelFaultDesc() {
        return fuelFaultDesc;
    }

    public static void setfrontTirePressureWarningActive(boolean frontTirePressureWarningActive){
        FaultStatus.frontTirePressureWarningActive = frontTirePressureWarningActive;
    }
    public static boolean getfrontTirePressureWarningActive() {
        return frontTirePressureWarningActive;
    }
    public static String getfrontTirePressureWarningDesc() {
        return frontTirePressureWarningDesc;
    }

    public static void setrearTirePressureWarningActive(boolean rearTirePressureWarningActive){
        FaultStatus.rearTirePressureWarningActive = rearTirePressureWarningActive;
    }
    public static boolean getrearTirePressureWarningActive() {
        return rearTirePressureWarningActive;
    }
    public static String getrearTirePressureWarningDesc() {
        return rearTirePressureWarningDesc;
    }

    public static void setfrontTirePressureCriticalActive(boolean frontTirePressureCriticalActive){
        FaultStatus.frontTirePressureCriticalActive = frontTirePressureCriticalActive;
    }
    public static boolean getfrontTirePressureCriticalActive() {
        return frontTirePressureCriticalActive;
    }
    public static String getfrontTirePressureCriticalDesc() {
        return frontTirePressureCriticalDesc;
    }
    public static void setfrontTirePressureCriticalNotificationActive(boolean frontTirePressureCriticalNotificationActive){
        FaultStatus.frontTirePressureCriticalNotificationActive = frontTirePressureCriticalNotificationActive;
    }
    public static boolean getfrontTirePressureCriticalNotificationActive() {
        return frontTirePressureCriticalNotificationActive;
    }

    public static void setrearTirePressureCriticalActive(boolean rearTirePressureCriticalActive){
        FaultStatus.rearTirePressureCriticalActive = rearTirePressureCriticalActive;
    }
    public static boolean getrearTirePressureCriticalActive() {
        return rearTirePressureCriticalActive;
    }
    public static String getrearTirePressureCriticalDesc() {
        return rearTirePressureCriticalDesc;
    }
    public static void setrearTirePressureCriticalNotificationActive(boolean rearTirePressureCriticalNotificationActive){
        FaultStatus.rearTirePressureCriticalNotificationActive = rearTirePressureCriticalNotificationActive;
    }
    public static boolean getrearTirePressureCriticalNotificationActive() {
        return rearTirePressureCriticalNotificationActive;
    }

    public static void setAddFrontLightOneActive(boolean addFrontLightOneActive){
        FaultStatus.addFrontLightOneActive = addFrontLightOneActive;
    }
    public static boolean getaddFrontLightOneActive() {
        return addFrontLightOneActive;
    }
    public static String getaddFrontLightOneDesc() {
        return addFrontLightOneDesc;
    }

    public static void setAddFrontLightTwoActive(boolean addFrontLightTwoActive){
        FaultStatus.addFrontLightTwoActive = addFrontLightTwoActive;
    }
    public static boolean getaddFrontLightTwoActive() {
        return addFrontLightTwoActive;
    }
    public static String getaddFrontLightTwoDesc() {
        return addFrontLightTwoDesc;
    }

    public static void setDaytimeRunningActive(boolean daytimeRunningActive){
        FaultStatus.daytimeRunningActive = daytimeRunningActive;
    }
    public static boolean getdaytimeRunningActive() {
        return daytimeRunningActive;
    }
    public static String getdaytimeRunninDesc() {
        return daytimeRunningDesc;
    }

    public static void setfrontLeftSignalActive(boolean frontLeftSignalActive){
        FaultStatus.frontLeftSignalActive = frontLeftSignalActive;
    }
    public static boolean getfrontLeftSignalActive() {
        return frontLeftSignalActive;
    }
    public static String getfrontLeftSignalDesc() {
        return frontLeftSignalDesc;
    }

    public static void setfrontRightSignalActive(boolean frontRightSignalActive){
        FaultStatus.frontRightSignalActive = frontRightSignalActive;
    }
    public static boolean getfrontRightSignalActive() {
        return frontRightSignalActive;
    }
    public static String getfrontRightSignalDesc() {
        return frontRightSignalDesc;
    }

    public static void setrearLeftSignalActive(boolean rearLeftSignalActive){
        FaultStatus.rearLeftSignalActive = rearLeftSignalActive;
    }
    public static boolean getrearLeftSignalActive() {
        return rearLeftSignalActive;
    }
    public static String getrearLeftSignalDesc() {
        return rearLeftSignalDesc;
    }

    public static void setrearRightSignalActive(boolean rearRightSignalActive){
        FaultStatus.rearRightSignalActive = rearRightSignalActive;
    }
    public static boolean getrearRightSignalActive() {
        return rearRightSignalActive;
    }
    public static String getrearRightSignalDesc() {
        return rearRightSignalDesc;
    }

    public static void setFrontParkingLightOneActive(boolean frontParkingLightOneActive){
        FaultStatus.frontParkingLightOneActive = frontParkingLightOneActive;
    }
    public static boolean getfrontParkingLightOneActive() {
        return frontParkingLightOneActive;
    }
    public static String getfrontParkingLightOneDesc() {
        return frontParkingLightOneDesc;
    }

    public static void setFrontParkingLightTwoActive(boolean frontParkingLightTwoActive){
        FaultStatus.frontParkingLightTwoActive = frontParkingLightTwoActive;
    }
    public static boolean getfrontParkingLightTwoActive() {
        return frontParkingLightTwoActive;
    }
    public static String getfrontParkingLightTwoDesc() {
        return frontParkingLightTwoDesc;
    }

    public static void setLowBeamActive(boolean lowBeamActive){
        FaultStatus.lowBeamActive = lowBeamActive;
    }
    public static boolean getlowBeamActive() {
        return lowBeamActive;
    }
    public static String getLowBeamDesc() {
        return lowBeamDesc;
    }

    public static void setHighBeamActive(boolean highBeamActive){
        FaultStatus.highBeamActive = highBeamActive;
    }
    public static boolean gethighBeamActive() {
        return highBeamActive;
    }
    public static String getHighBeamDesc() {
        return highBeamDesc;
    }

    public static void setRearLightActive(boolean rearLightActive){
        FaultStatus.rearLightActive = rearLightActive;
    }
    public static boolean getrearLightActive() {
        return rearLightActive;
    }
    public static String getRearLightDescDesc() {
        return rearLightDesc;
    }

    public static void setBrakeLightActive(boolean brakeLightActive){
        FaultStatus.brakeLightActive = brakeLightActive;
    }
    public static boolean getBrakeLightActive() {
        return brakeLightActive;
    }
    public static String getBrakeLightDescDesc() {
        return brakeLightDesc;
    }

    public static void setLicenseLightActive(boolean licenseLightActive){
        FaultStatus.licenseLightActive = licenseLightActive;
    }
    public static boolean getLicenseLightActive() {
        return licenseLightActive;
    }
    public static String getLicenseLightDescDesc() {
        return licenseLightDesc;
    }

    public static void setRearFogLightActive(boolean rearFogLightActive){
        FaultStatus.rearFogLightActive = rearFogLightActive;
    }
    public static boolean getRearFogLightActive() {
        return rearFogLightActive;
    }
    public static String getRearFogLightDescDesc() {
        return rearFogLightDesc;
    }

    public static void setAddDippedLightActive(boolean addDippedLightActive){
        FaultStatus.addDippedLightActive = addDippedLightActive;
    }
    public static boolean getAddDippedLightActive() {
        return addDippedLightActive;
    }
    public static String getAddDippedLightDescDesc() {
        return addDippedLightDesc;
    }

    public static void setAddBrakeLightActive(boolean addBrakeLightActive){
        FaultStatus.addBrakeLightActive = addBrakeLightActive;
    }
    public static boolean getAddBrakeLightActive() {
        return addBrakeLightActive;
    }
    public static String getAddBrakeLightDescDesc() {
        return addBrakeLightDesc;
    }

    public static void setFrontLampOneLightActive(boolean frontLampOneLightActive){
        FaultStatus.frontLampOneLightActive = frontLampOneLightActive;
    }
    public static boolean getFrontLampOneLightActive() {
        return frontLampOneLightActive;
    }
    public static String getFrontLampOneLightDescDesc() {
        return frontLampOneLightDesc;
    }

    public static void setFrontLampTwoLightActive(boolean frontLampTwoLightActive){
        FaultStatus.frontLampTwoLightActive = frontLampTwoLightActive;
    }
    public static boolean getFrontLampvLightActive() {
        return frontLampTwoLightActive;
    }
    public static String getFrontLampTwoLightDescDesc() {
        return frontLampTwoLightDesc;
    }

    public static void seticeWarnActive(boolean iceWarningActive){
        FaultStatus.iceWarningActive = iceWarningActive;
    }
    public static boolean geticeWarningActive() {
        return iceWarningActive;
    }
    public static String geticeWarningDesc() {
        return iceWarningDesc;
    }

    public static void setGeneralFlashingYellowActive(boolean generalFlashingYellowActive){
        FaultStatus.generalFlashingYellowActive = generalFlashingYellowActive;
    }
    public static boolean getgeneralFlashingYellowActive() {
        return generalFlashingYellowActive;
    }
    public static String getGeneralFlashingYellowDesc() {
        return generalFlashingYellowDesc;
    }

    public static void setGeneralShowsYellowActive(boolean generalShowsYellowActive){
        FaultStatus.generalShowsYellowActive = generalShowsYellowActive;
    }
    public static boolean getgeneralShowsYellowActive() {
        return generalShowsYellowActive;
    }
    public static String getGeneralShowsYellowDesc() {
        return generalShowsYellowDesc;
    }

    public static void setGeneralFlashingRedActive(boolean generalFlashingRedActive){
        FaultStatus.generalFlashingRedActive = generalFlashingRedActive;
    }
    public static boolean getgeneralFlashingRedActive() {
        return generalFlashingRedActive;
    }
    public static String getGeneralFlashingRedDesc() {
        return generalFlashingRedDesc;
    }
    public static void setGeneralFlashingRedNotificationActive(boolean generalFlashingRedNotificationActive){
        FaultStatus.generalFlashingRedNotificationActive = generalFlashingRedNotificationActive;
    }
    public static boolean getgeneralFlashingRedNotificationActive() {
        return generalFlashingRedNotificationActive;
    }

    public static void setGeneralShowsRedActive(boolean generalShowsRedActive){
        FaultStatus.generalShowsRedActive = generalShowsRedActive;
    }
    public static boolean getgeneralShowsRedActive() {
        return generalShowsRedActive;
    }
    public static String getGeneralShowsRedDesc() {
        return generalShowsRedDesc;
    }
    public static void setGeneralShowsRedNotificationActive(boolean generalShowsRedNotificationActive){
        FaultStatus.generalShowsRedNotificationActive = generalShowsRedNotificationActive;
    }
    public static boolean getgeneralShowsRedNotificationActive() {
        return generalShowsRedNotificationActive;
    }

    public static void setOilLowActive(boolean oilLowActive){
        FaultStatus.oilLowActive = oilLowActive;
    }
    public static boolean getOilLowActive() {
        return oilLowActive;
    }
    public static String getOilLowDesc() {
        return oilLowDesc;
    }

    // Utility functions
    public static void clear(){
        // Debug Faults
        /*
        uartOverflowActive = false;
        uartFrameActive = false;
        uartLogicActive = false;
        */

        // Motorcycle Faults
        absSelfDiagActive = false;
        absDeactivatedActive = false;
        absErrorActive = false;
        ascSelfDiagActive = false;
        ascInterventionActive = false;
        ascDeactivatedActive = false;
        ascErrorActive = false;
        fuelFaultActive = false;
        frontTirePressureWarningActive = false;
        rearTirePressureWarningActive = false;
        frontTirePressureCriticalActive = false;
        rearTirePressureCriticalActive = false;
        addFrontLightOneActive = false;
        addFrontLightTwoActive = false;
        daytimeRunningActive = false;
        frontLeftSignalActive = false;
        frontRightSignalActive = false;
        rearLeftSignalActive = false;
        rearRightSignalActive = false;
        frontParkingLightOneActive = false;
        frontParkingLightTwoActive = false;
        lowBeamActive = false;
        highBeamActive = false;
        rearLightActive = false;
        brakeLightActive = false;
        licenseLightActive = false;
        rearFogLightActive = false;
        addDippedLightActive = false;
        addBrakeLightActive = false;
        frontLampOneLightActive = false;
        frontLampTwoLightActive = false;
        iceWarningActive = false;
        generalFlashingYellowActive = false;
        generalShowsYellowActive = false;
        generalFlashingRedActive = false;
        generalShowsRedActive = false;
        oilLowActive = false;
    }
}

