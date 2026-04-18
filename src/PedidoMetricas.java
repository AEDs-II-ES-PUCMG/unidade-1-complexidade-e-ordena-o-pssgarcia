import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

public final class PedidoMetricas {

    private PedidoMetricas() {}

    public static double valorFinal(Pedido p) {
        return valorFinal(p, null);
    }

    public static double valorFinal(Pedido p, Map<Pedido, Double> cache) {
        if (cache != null && cache.containsKey(p)) return cache.get(p);

        double total = 0.0;
        ItemDePedido[] itens = p.getTotalItens();
        for (ItemDePedido it : itens) {
            if (it == null) continue;
            total += it.getQuantidade() * it.getPrecoUnitarioRegistrado(); 
        }

        if (cache != null) cache.put(p, total);
        return total;
    }

    public static int volumeTotal(Pedido p) {
        return volumeTotal(p, null);
    }

    public static int volumeTotal(Pedido p, Map<Pedido, Integer> cache) {
        if (cache != null && cache.containsKey(p)) return cache.get(p);

        int vol = 0;
        ItemDePedido[] itens = p.getTotalItens(); 
        for (ItemDePedido it : itens) {
            if (it == null) continue;
            vol += it.getQuantidade();
        }

        if (cache != null) cache.put(p, vol);
        return vol;
    }

    public static int codigoPrimeiroItem(Pedido p) {
        ItemDePedido[] itens = p.getTotalItens();
        for (ItemDePedido it : itens) {
            if (it != null && it.getProduto() != null) {
                return it.getProduto().hashCode();
            }
        }
        return Integer.MAX_VALUE;
    }

    public static double economiaReal(Pedido p, Map<Pedido, Double> cache) {
        if (cache != null && cache.containsKey(p)) return cache.get(p);

        double economia = 0.0;
        ItemDePedido[] itens = p.getTotalItens();
        for (ItemDePedido it : itens) {
            if (it == null || it.getProduto() == null) continue;
            double precoAtualCatalogo = it.getProduto().getPrecoVenda();
            double precoRegistrado = it.getPrecoUnitarioRegistrado();
            economia += (precoAtualCatalogo - precoRegistrado) * it.getQuantidade();
        }

        if (cache != null) cache.put(p, economia);
        return economia;
    }

    public static int codigoPedido(Pedido p) {
        return p.getIdPrimeiroProduto();
    }

    public static long dataEpoch(Pedido p) {
        LocalDate d = p.getDataPedido();
        return d.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }
}