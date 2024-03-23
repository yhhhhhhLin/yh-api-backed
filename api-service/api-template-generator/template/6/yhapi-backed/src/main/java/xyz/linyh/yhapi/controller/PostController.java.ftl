package ${groupName}.${artifactName}.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${groupName}.${artifactName}.annotation.AuthCheck;
import ${groupName}.${artifactName}.common.BaseResponse;
import ${groupName}.${artifactName}.common.DeleteRequest;
import ${groupName}.${artifactName}.common.ErrorCode;
import ${groupName}.${artifactName}.common.ResultUtils;
import ${groupName}.${artifactName}.constant.CommonConstant;
import ${groupName}.${artifactName}.exception.BusinessException;
import ${groupName}.${artifactName}.model.dto.post.PostAddRequest;
import ${groupName}.${artifactName}.model.dto.post.PostQueryRequest;
import ${groupName}.${artifactName}.model.dto.post.PostUpdateRequest;
import ${groupName}.${artifactName}.model.entity.Post;
<#if needUserExample>
import ${groupName}.${artifactName}.model.entity.User;
import ${groupName}.${artifactName}.service.UserService;
</#if>
import ${groupName}.${artifactName}.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 * @author ${authorName}
 */
@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {

    @Resource
    private PostService postService;

<#if needUserExample>
    @Resource
    private UserService userService;
</#if>

    // region 增删改查

    /**
     * 创建
     *
     * @param postAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest, HttpServletRequest request) {
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postAddRequest, post);
        // 校验
        postService.validPost(post, true);
        // todo 这里需要获取id
    <#if needUserExample>
        User loginUser = userService.getLoginUser(request);
        post.setUserId(loginUser.getId());
        <#else>
        post.setUserId(0L);
    </#if>
        boolean result = postService.save(post);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newPostId = post.getId();
        return ResultUtils.success(newPostId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePost(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        <#if needUserExample>
            User user = userService.getLoginUser(request);
        <#else>
            // 需要获取用户id
            // User user = 0L;
        </#if>
        long id = deleteRequest.getId();
        // 判断是否存在
        Post oldPost = postService.getById(id);
        if (oldPost == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        <#if needUserExample>
        if (!oldPost.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        <#else>
        // TODO 需要判断本人或管理员才可以删除
        // if (!oldPost.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
        //    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        // }
        </#if>
        boolean b = postService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param postUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updatePost(@RequestBody PostUpdateRequest postUpdateRequest,
                                            HttpServletRequest request) {
        if (postUpdateRequest == null || postUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postUpdateRequest, post);
        // 参数校验
        postService.validPost(post, false);
        <#if needUserExample>
        User user = userService.getLoginUser(request);
        <#else>
        // 需要获取用户id
        // User user = 0L;
        </#if>
        long id = postUpdateRequest.getId();
        // 判断是否存在
        Post oldPost = postService.getById(id);
        if (oldPost == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        <#if needUserExample>
        if (!oldPost.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        <#else>
        // TODO 需要判断本人或管理员才可以删除
        // if (!oldPost.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
        //    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        // }
        </#if>
        boolean result = postService.updateById(post);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Post> getPostById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = postService.getById(id);
        return ResultUtils.success(post);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param postQueryRequest
     * @return
     */
    <#if needUserExample>
    @AuthCheck(mustRole = "admin")
    </#if>
    @GetMapping("/list")
    public BaseResponse<List<Post>> listPost(PostQueryRequest postQueryRequest) {
        Post postQuery = new Post();
        if (postQueryRequest != null) {
            BeanUtils.copyProperties(postQueryRequest, postQuery);
        }
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>(postQuery);
        List<Post> postList = postService.list(queryWrapper);
        return ResultUtils.success(postList);
    }

    /**
     * 分页获取列表
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Post>> listPostByPage(PostQueryRequest postQueryRequest, HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post postQuery = new Post();
        BeanUtils.copyProperties(postQueryRequest, postQuery);
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();
        String content = postQuery.getContent();
        // content 需支持模糊搜索
        postQuery.setContent(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>(postQuery);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<Post> postPage = postService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(postPage);
    }

    // endregion

}
