package com.uniwin.webkey.core.ui;

import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IOperationManager;
import com.uniwin.webkey.core.model.Operation;
import com.uniwin.webkey.system.ui.WorkbenchWin;

public class OperationUpdataWin extends Window
{
    private IOperationManager operationManager = (IOperationManager) SpringUtil
                                                       .getBean("operationManager");

    private List<Operation>   operationList;

    private Operation         operation;

    public OperationUpdataWin()
    {
        Map map = Executions.getCurrent().getArg();
        operation = (Operation) map.get("operation");
    }

    public List<Operation> getOperationList()
    {
        return operationList;
    }

    public void setOperationList(List<Operation> operationList)
    {
        this.operationList = operationList;
    }

    /**
     * 修改操作信息
     */
    public void updataOperation()
    {
        Operation operationUp = new Operation();
        operationUp.setOperationId(operation.getOperationId());
        operationUp.setName(((Textbox) this.getFellow("name")).getText());
        operationUp.setOpCode(((Textbox) this.getFellow("code")).getText());
        operationUp.setDescription(((Textbox) this.getFellow("description"))
                .getText());
        try
        {
            operationManager.update(operationUp);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.changesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            ((WorkbenchWin) this.getDesktop().getAttribute("WorkbenchWin"))
                    .reOpenTab();
            this.detach();
        } catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.changefailedtoadmin"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.ERROR);
            } catch (Exception e2)
            {
                e2.printStackTrace();
            }
        }

    }

    public Operation getOperation()
    {
        return operation;
    }

    public void setOperation(Operation operation)
    {
        this.operation = operation;
    }

}
