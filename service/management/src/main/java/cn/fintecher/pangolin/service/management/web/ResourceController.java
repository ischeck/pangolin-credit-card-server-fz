package cn.fintecher.pangolin.service.management.web;

import cn.fintecher.pangolin.entity.managentment.Resource;
import cn.fintecher.pangolin.service.management.model.response.ResourceResponse;
import cn.fintecher.pangolin.service.management.repository.ResourceRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

/**
 * Created by ChenChang on 2018/6/7
 */
@RestController
@RequestMapping("/api/resource")
@Api(value = "资源相关", description = "资源相关")
public class ResourceController {
    private final Logger log = LoggerFactory.getLogger(ResourceController.class);
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private ModelMapper modelMapper;

    @ApiOperation(value = "获取所有资源", notes = "获取所有资源")
    @GetMapping("/findAll")
    public ResponseEntity<List<ResourceResponse>> getAbility() {
        log.debug("findAll Resource");
        Type listType = new TypeToken<List<ResourceResponse>>() {
        }.getType();
        List<ResourceResponse> resourceResponses = modelMapper.map(resourceRepository.findAll(), listType);
        return Optional.ofNullable(resourceResponses)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @ApiOperation(value = "添加前端资源路由", notes = "添加前端资源路由")
    @GetMapping("/setUrl")
    public ResponseEntity setUrl(@RequestParam String id,
                                 String url) {

        Optional<Resource> byId = resourceRepository.findById(id);
        Resource resource = byId.get();
        resource.setUrl(url);
        resourceRepository.save(resource);
        return ResponseEntity.ok().body(null);
    }


    //    @PostMapping("/saveResource")
//    public ResponseEntity<Resource> saveResource(@RequestBody Resource resource) throws URISyntaxException {
//        log.debug("REST request to save Resource : {}", resource);
//        Resource result = resourceRepository.save(resource);
//        return ResponseEntity.created(new URI("/api/resource/" + result.getId()))
//                .body(result);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteResource(@PathVariable String id) {
//        log.debug("REST request to delete resource : {}", id);
//        resourceRepository.deleteById(id);
//        return ResponseEntity.ok().build();
//    }
//    @ApiOperation(value = "资源类型数据字典", notes = "资源类型数据字典")
//    @GetMapping("/type")
//    public ResponseEntity<List<DataDict>> type() {
//        return ResponseEntity.ok().body(Arrays.stream(Resource.Type.values()).map(e -> new DataDict(e.name(), e.getChinese())).collect(Collectors.toList()));
//    }

}
