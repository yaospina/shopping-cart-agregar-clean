package isi.shoppingCart.usecases.services;

import isi.shoppingCart.entities.Cart;
import isi.shoppingCart.entities.CartItem;
import isi.shoppingCart.entities.Product;
import isi.shoppingCart.usecases.ports.CartRepository;
import isi.shoppingCart.usecases.ports.ProductRepository;

import java.util.List;

public class ConfirmarCompraUseCase {
    private CartRepository cartRepository;
    private ProductRepository productRepository;

    public ConfirmarCompraUseCase(CartRepository cartRepository,
                                  ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public String execute() {
        Cart cart = cartRepository.getCart();

        if (cart.isEmpty()) {
            return "No se puede confirmar la compra. El carrito esta vacío.";
        }

        List<CartItem> items = cart.getItems();
        int i;

        // Validar stock suficiente para todos los ítems
        for (i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            Product product = productRepository.findById(item.getProduct().getId());

            if (product == null) {
                return "Producto no encontrado: " + item.getProduct().getName();
            }

            if (item.getQuantity() > product.getAvailableQuantity()) {
                return "Stock insuficiente para el producto: " + product.getName()
                        + ". Disponible: " + product.getAvailableQuantity()
                        + ", en carrito: " + item.getQuantity();
            }
        }

        // descontar stock
        for (i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            Product product = productRepository.findById(item.getProduct().getId());
            int j;

            for (j = 0; j < item.getQuantity(); j++) {
                product.decreaseAvailableQuantity();
            }
        }

        // vaciar el carrito y guardar
        cart.clear();
        cartRepository.save(cart);

        return "";
    }
}