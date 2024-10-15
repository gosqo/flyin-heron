package com.gosqo.flyinheron.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MemberProfileImageManager extends AbstractImageManager {
    static final String MEMBER_PROFILE_IMAGE_PATH = LOCAL_STORAGE_PATH + "member/";

    @Override
    protected String renameFile(String originalFilename) {
        String spaceReplaced = originalFilename.replaceAll(" ", "-");

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = format.format(new Date());

        return formattedDate + "_" + spaceReplaced;
    }
}
