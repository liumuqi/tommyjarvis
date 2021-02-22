package origin.rocketmq;

/**
 * @Author:mlamp
 * @Date: 2021/2/22
 * @Desc:
 **/
public class SendDingDing {
//    public ReturnVo<String> sendDingCorpconversationMessage(MessageDto messageDto) throws Exception {
//        ReturnVo returnVo = new ReturnVo<>(ReturnCode.FAIL, "失败");
//        if (CollectionUtils.isEmpty(messageDto.getUseridList()) || messageDto.getToAllUser() == null){
//            return new ReturnVo<>(ReturnCode.FAIL, "userIdList,toAllUser,不能都为空");
//        }
//        try {
//            if (!CollectionUtils.isEmpty(messageDto.getUseridList())){
//                ReturnVo<String> accessToken = dingtalkTokenService.getAccessToken();
//                if (!accessToken.isSuccess()){
//                    return accessToken;
//                }
//                DingTalkClient client = new DefaultDingTalkClient(DingtalkUrlConstant.URL_TOPAPI_MESSAGE_CORPCONVERSATION_ASYNCSEND_V2);
//                OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
//                request.setAgentId(dingtalkProperties.getAgentId());
//                StringBuffer userIds = new StringBuffer("");
//                for (String userid : messageDto.getUseridList()) {
//                    userIds.append(userid).append(",");
//                }
//                if (userIds.length() > 0){
//                    userIds = userIds.deleteCharAt(userIds.length() - 1);
//                }
//                request.setUseridList(userIds.toString());
//                request.setToAllUser(false);
//                OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
//                if (messageDto.getItemDto() != null){
//                    MessageItemDto itemDto = messageDto.getItemDto();
//                    String msgtype = itemDto.getMsgtype().getCode();
//                    msg.setMsgtype(msgtype);
//                    switch (msgtype) {
//                        case "text":
//                            msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
//                            msg.getText().setContent(itemDto.getContent());
//                            request.setMsg(msg);
//                            break;
//                        case "image":
//                            //发送图片
//                            ReturnVo<String> uploadPicResponse = uploadPic(itemDto, accessToken);
//                            String mediaId;
//                            if (uploadPicResponse.isSuccess()){
//                                mediaId = uploadPicResponse.getData();
//                            } else {
//                                return new ReturnVo<>(ReturnCode.FAIL, uploadPicResponse.getMsg());
//                            }
//                            msg.setImage(new OapiMessageCorpconversationAsyncsendV2Request.Image());
//                            msg.getImage().setMediaId(mediaId);
//                            msg.setImage(new OapiMessageCorpconversationAsyncsendV2Request.Image());
//                            request.setMsg(msg);
//                            break;
//                        case "file":
//                            ReturnVo<String> uploadFileResponse = uploadPic(itemDto, accessToken);
//                            String fileMediaId = null;
//                            if (uploadFileResponse.isSuccess()){
//                                fileMediaId = uploadFileResponse.getData();
//                            } else {
//                                return new ReturnVo<>(ReturnCode.FAIL, uploadFileResponse.getMsg());
//                            }
//                            msg.setFile(new OapiMessageCorpconversationAsyncsendV2Request.File());
//                            msg.getFile().setMediaId(fileMediaId);
//                            request.setMsg(msg);
//                            break;
//                        case "link":
//                            msg.setLink(new OapiMessageCorpconversationAsyncsendV2Request.Link());
//                            msg.getLink().setTitle(itemDto.getTitle());
//                            msg.getLink().setText(itemDto.getContent());
//                            msg.getLink().setMessageUrl(itemDto.getMessageUrl());
//                            msg.getLink().setPicUrl(itemDto.getPicUrl());
//                            request.setMsg(msg);
//                            break;
//                        default:
//                            break;
//                    }
//                }
//                OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request, accessToken.getData());
//                if (response.isSuccess()){
//                    returnVo = new ReturnVo<>(ReturnCode.SUCCESS, "发送成功");
//                    returnVo.setData(response.getTaskId()+"");
//                    return returnVo;
//                } else {
//                    return new ReturnVo<>(ReturnCode.FAIL, response.getErrmsg());
//                }
//            }
//
//        } catch (ApiException e) {
//            logger.error("发送消息失败", e);
//            returnVo = new ReturnVo<>(ReturnCode.FAIL, "发送消息失败");
//        }
//        return returnVo;
//    }
}
