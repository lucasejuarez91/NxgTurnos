package com.ar.nxg.nxgappts.enums;

public enum SocialMediaTypeEnum {
    FACEBOOK("fb", "logo");

    public final String label;
    public final String pathLogo;

    private SocialMediaTypeEnum(String label, String pathLogo) {
        this.label = label;
        this.pathLogo = pathLogo;
    }
}
