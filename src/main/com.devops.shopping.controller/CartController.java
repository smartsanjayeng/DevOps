package com.devops.shopping.controller;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody CartItem cartItem) {
        cartService.addToCart(cartItem);
        return new ResponseEntity<>("Item added to cart", HttpStatus.OK);
    }
}
