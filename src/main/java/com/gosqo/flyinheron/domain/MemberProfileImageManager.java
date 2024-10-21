package com.gosqo.flyinheron.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MemberProfileImageManager extends DefaultImageManager {
    static final String MEMBER_IMAGE_DIR = LOCAL_STORAGE_DIR + "member/";
    private static MemberProfileImageManager instance;

    public static synchronized MemberProfileImageManager getInstance() {

        if (instance == null) {
            instance = new MemberProfileImageManager();
        }

        return instance;
    }

    @Override
    protected String renameFile(String originalFilename) {
        String spaceReplaced = originalFilename.replaceAll(" ", "-");

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = format.format(new Date());

        return formattedDate + "_" + spaceReplaced;
    }
}
