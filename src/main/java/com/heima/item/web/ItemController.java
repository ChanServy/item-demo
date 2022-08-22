package com.heima.item.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.heima.item.pojo.Item;
import com.heima.item.pojo.ItemStock;
import com.heima.item.pojo.PageDTO;
import com.heima.item.service.IItemService;
import com.heima.item.service.IItemStockService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("item")
public class ItemController {

    @Resource
    private IItemService itemService;
    @Resource
    private IItemStockService stockService;
    @Resource
    private Cache<Long, Item> itemCache;
    @Resource
    private Cache<Long, ItemStock> stockCache;

    /**
     * 分页查询
     */
    @GetMapping("list")
    public PageDTO queryItemPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size){
        // 分页查询商品
        Page<Item> result = itemService.query()
                .ne("status", 3)
                .page(new Page<>(page, size));

        // 查询库存
        List<Item> list = result.getRecords().stream().peek(item -> {
            ItemStock stock = stockService.getById(item.getId());
            item.setStock(stock.getStock());
            item.setSold(stock.getSold());
        }).collect(Collectors.toList());

        // 封装返回
        return new PageDTO(result.getTotal(), list);
    }

    /**
     * 保存
     */
    @PostMapping
    public void saveItem(@RequestBody Item item){
        itemService.saveItem(item);
    }

    /**
     * 修改
     */
    @PutMapping
    public void updateItem(@RequestBody Item item) {
        itemService.updateById(item);
    }

    /**
     * 更新库存
     */
    @PutMapping("stock")
    public void updateStock(@RequestBody ItemStock itemStock){
        stockService.updateById(itemStock);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id){
        itemService.update().set("status", 3).eq("id", id).update();
    }

    /**
     * 根据id查询
     */
    @GetMapping("/{id}")
    public Item findById(@PathVariable("id") Long id){
        return itemCache.get(id, (key) -> itemService.query()
                .ne("status", 3).eq("id", key)
                .one());
    }

    /**
     * 根据id查询库存
     */
    @GetMapping("/stock/{id}")
    public ItemStock findStockById(@PathVariable("id") Long id){
        return stockCache.get(id, (key) -> stockService.getById(key));
    }
}
