package com.uniwin.webkey.core.ui;

import java.util.List;

import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IOperationManager;
import com.uniwin.webkey.core.model.Operation;
import com.uniwin.webkey.system.ui.WorkbenchWin;

public class OperationAddWin extends Window
{
    private IOperationManager operationManager = (IOperationManager) SpringUtil
                                                       .getBean("operationManager");

    private List<Operation>   operationList;

    public OperationAddWin()
    {
        try
        {
            operationList = operationManager.getOperationListOrderbyId();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public List<Operation> getOperationList()
    {
        return operationList;
    }

    public void setOperationList(List<Operation> operationList)
    {
        this.operationList = operationList;
    }


    public void addOperation()
    {
        Operation operation = new Operation();
        operation.setName(((Textbox) this.getFellow("name")).getText());
        operation.setOpCode(((Textbox) this.getFellow("code")).getText());
        operation.setDescription(((Textbox) this.getFellow("description"))
                .getText());
        try
        {
            operationManager.add(operation);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.savesuccess"),
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
                        .getLabel("system.commom.ui.savefailed"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.ERROR);
            } catch (Exception e2)
            {
                e2.printStackTrace();
            }
        }

    }
}
