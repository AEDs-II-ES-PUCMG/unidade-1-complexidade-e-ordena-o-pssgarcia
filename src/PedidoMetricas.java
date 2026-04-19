import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

/**
 * Encapsula as métricas calculadas de um Pedido.
 * O array de itens é obtido uma única vez no construtor e
 * reutilizado por todos os métodos da instância.
 * Os valores calculados são armazenados internamente (lazy cache),
 * evitando recomputações dentro da mesma comparação.
 */
public class PedidoMetricas {

    private final Pedido pedido;
    private final ItemDePedido[] itens;
    private final int qtdItens;

    // Lazy cache interno
    private Double _valorFinal;
    private Integer _volumeTotal;
    private Integer _codigoPrimeiroItem;
    private Double _economiaReal;

    public PedidoMetricas(Pedido pedido) {
        this.pedido   = pedido;
        this.itens    = pedido.getItens();
        this.qtdItens = pedido.getQtdItens();
    }

    /** Retorna o valor final do pedido (com desconto à vista se aplicável). */
    public double valorFinal() {
        if (_valorFinal == null)
            _valorFinal = pedido.valorFinal();
        return _valorFinal;
    }

    /** Retorna o total de unidades físicas encomendadas. */
    public int volumeTotal() {
        if (_volumeTotal == null) {
            int vol = 0;
            for (int i = 0; i < qtdItens; i++)
                if (itens[i] != null) vol += itens[i].getQuantidade();
            _volumeTotal = vol;
        }
        return _volumeTotal;
    }

    /** Retorna o hashCode (ID) do primeiro produto do pedido. */
    public int codigoPrimeiroItem() {
        if (_codigoPrimeiroItem == null) {
            _codigoPrimeiroItem = Integer.MAX_VALUE;
            for (int i = 0; i < qtdItens; i++) {
                if (itens[i] != null && itens[i].getProduto() != null) {
                    _codigoPrimeiroItem = itens[i].getProduto().hashCode();
                    break;
                }
            }
        }
        return _codigoPrimeiroItem;
    }

    /**
     * Retorna o índice de economia: diferença entre o valor de catálogo
     * atual e o valor efetivamente pago (preço congelado).
     */
    public double economiaReal() {
        if (_economiaReal == null) {
            double eco = 0.0;
            for (int i = 0; i < qtdItens; i++) {
                if (itens[i] == null || itens[i].getProduto() == null) continue;
                eco += (itens[i].getProduto().valorDeVenda() - itens[i].getPrecoVenda())
                       * itens[i].getQuantidade();
            }
            _economiaReal = eco;
        }
        return _economiaReal;
    }

    /** Retorna o ID do pedido (usado como desempate final nos critérios B e C). */
    public int codigoPedido() {
        return pedido.getIdPedido();
    }

    /** Retorna a data do pedido em epoch seconds (para ordenação). */
    public long dataEpoch() {
        LocalDate d = pedido.getDataPedido();
        return d.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * Helper estático: obtém (ou cria) a instância de métricas
     * para o pedido informado, usando o mapa como cache externo.
     */
    public static PedidoMetricas de(Pedido p, Map<Pedido, PedidoMetricas> cache) {
        return cache.computeIfAbsent(p, PedidoMetricas::new);
    }
}