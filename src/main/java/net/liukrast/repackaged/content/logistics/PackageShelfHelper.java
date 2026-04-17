package net.liukrast.repackaged.content.logistics;

import com.google.common.collect.Lists;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packager.repackager.PackageRepackageHelper;
import net.liukrast.deployer.lib.logistics.OrderStockTypeData;
import net.liukrast.deployer.lib.logistics.packager.GenericPackageItem;
import net.liukrast.deployer.lib.registry.DeployerDataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;

public class PackageShelfHelper extends PackageRepackageHelper {
    private final IntSupplier sizeGetter;
    public PackageShelfHelper(IntSupplier sizeGetter) {
        this.sizeGetter = sizeGetter;
    }

    @Override
    public int addPackageFragment(ItemStack box) {
        int collectedOrderId = PackageItem.getOrderId(box);
        if (collectedOrderId == -1)
            return -1;

        List<ItemStack> collectedOrder = collectedPackages.computeIfAbsent(collectedOrderId, $ -> Lists.newArrayList());
        collectedOrder.add(box);

        if (!isOrderComplete(collectedOrderId))
            return -1;

        return collectedOrderId;
    }

    @Override
    public List<BigItemStack> repack(int orderId, RandomSource r) {
        List<BigItemStack> exportingPackages = new ArrayList<>();
        var li = collectedPackages.get(orderId);
        for (ItemStack itemStack : li) {
            exportingPackages.add(new BigItemStack(itemStack.copy()));
        }
        return exportingPackages;
    }

    private record Data(boolean stockFinal, boolean linkFinal, boolean fragFinal) {}

    private boolean isOrderComplete(int orderId) {
        Map<Integer, Map<Integer, Map<Integer, Data>>> dataMap = new HashMap<>();
        if(collectedPackages.get(orderId).size() > sizeGetter.getAsInt()) return false;
        for(ItemStack box : collectedPackages.get(orderId)) {
            var stockData = box.getOrDefault(DeployerDataComponents.ORDER_STOCK_TYPE_DATA, OrderStockTypeData.EMPTY);
            final int stockIndex = stockData.index();
            final int linkIndex;
            final int fragIndex;
            final boolean stockFinal = stockData.isFinal();
            final boolean linkFinal;
            final boolean fragFinal;

            if(box.getItem() instanceof GenericPackageItem item) {
                var data = box.get(item.getType().packageHandler().packageOrderData());
                linkIndex = data == null ? 0 : data.linkIndex();
                fragIndex = data == null ? 0 : data.fragmentIndex();
                linkFinal = data != null && data.isFinalLink();
                fragFinal = data != null && data.isFinal();
            } else {
                var data = box.get(AllDataComponents.PACKAGE_ORDER_DATA);
                linkIndex = data == null ? 0 : data.linkIndex();
                fragIndex = data == null ? 0 : data.fragmentIndex();
                linkFinal = data != null && data.isFinalLink();
                fragFinal = data != null && data.isFinal();
            }

            dataMap.computeIfAbsent(stockIndex, k -> new HashMap<>())
                    .computeIfAbsent(linkIndex, k -> new HashMap<>())
                    .put(fragIndex, new Data(stockFinal, linkFinal, fragFinal));
        }

        boolean finalStockReached = false;
        int stockIndex = 0;
        while(!finalStockReached) {
            Map<Integer, Map<Integer, Data>> links = dataMap.get(stockIndex);
            if(links == null) return false;
            boolean finalLinkReached = false;
            int linkIndex = 0;
            while(!finalLinkReached) {
                Map<Integer, Data> fragments = links.get(linkIndex);
                if(fragments == null) return false;
                boolean finalFragmentReached = false;
                int fragmentIndex = 0;
                while(!finalFragmentReached) {
                    Data data = fragments.get(fragmentIndex);
                    if(data == null) return false;
                    if(data.fragFinal) finalFragmentReached = true;
                    if(data.linkFinal) finalLinkReached = true;
                    if(data.stockFinal) finalStockReached = true;
                    fragmentIndex++;
                }
                linkIndex++;
            }
            stockIndex++;
        }
        return true;
    }
}
