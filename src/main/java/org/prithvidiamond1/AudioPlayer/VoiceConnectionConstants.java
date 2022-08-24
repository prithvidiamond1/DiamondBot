package org.prithvidiamond1.AudioPlayer;

public final class VoiceConnectionConstants {
    public interface VoiceConnectionConstantsInterface{
    }

    public static enum VoiceConnectionStatus implements VoiceConnectionConstantsInterface{
        /**
         * State for successful audio connection
         */
        SUCCESSFUL,
        /**
         * State for unsuccessful audio connection
         */
        UNSUCCESSFUL,
        /**
         * State for a pre-existing audio connection
         */
        ALREADY_CONNECTED
    }

    public static enum VoiceChannelConnectionStatus implements VoiceConnectionConstantsInterface{
        JOIN_VOICE_CHANNEL(VoiceConnectionStatus.SUCCESSFUL),
        ALREADY_JOINED_VOICE_CHANNEL(VoiceConnectionStatus.ALREADY_CONNECTED),
        NO_FREE_VOICE_CHANNELS(VoiceConnectionStatus.UNSUCCESSFUL),
        NO_ACCESSIBLE_VOICE_CHANNELS(VoiceConnectionStatus.UNSUCCESSFUL),
        ERROR_DURING_CONNECTION(VoiceConnectionStatus.UNSUCCESSFUL);

        private final VoiceConnectionStatus connectionStatus;

        private VoiceChannelConnectionStatus(VoiceConnectionStatus connectionStatus){
            this.connectionStatus = connectionStatus;
        }

        public VoiceConnectionStatus getConnectionStatus(){
            return this.connectionStatus;
        }
    }
}
