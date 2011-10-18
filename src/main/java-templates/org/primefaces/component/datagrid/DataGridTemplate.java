	public static final String DATAGRID_CLASS = "ui-datagrid ui-widget";
    public static final String CONTENT_CLASS = "ui-datagrid-content";
	public static final String TABLE_CLASS = "ui-datagrid-data";
	public static final String TABLE_ROW_CLASS = "ui-datagrid-row";
	public static final String TABLE_COLUMN_CLASS = "ui-datagrid-column";

    public boolean isPagingRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_ajaxPaging");
    }

    protected void updatePaginationMetadata(FacesContext context) {
        ValueExpression firstVe = this.getValueExpression("first");
        ValueExpression rowsVe = this.getValueExpression("rows");
        ValueExpression pageVE = this.getValueExpression("page");

        if(firstVe != null)
            firstVe.setValue(context.getELContext(), getFirst());
        if(rowsVe != null)
            rowsVe.setValue(context.getELContext(), getRows());
        if(pageVE != null)
            pageVE.setValue(context.getELContext(), getPage());
    }

    @Override
    public void processDecodes(FacesContext context) {
		if(isPagingRequest(context)) {
            this.decode(context);

            updatePaginationMetadata(context);

            context.renderResponse();
        }
        else {
            super.processDecodes(context);
        }
	}

    public void calculatePage() {
        int rows = this.getRows();
        int currentPage = this.getPage();
        int numberOfPages = (int) Math.ceil(this.getRowCount() * 1d / rows);

        if(currentPage > numberOfPages && numberOfPages > 0) {
            currentPage = numberOfPages;

            this.setPage(currentPage);
            this.setFirst((currentPage-1) * rows);
        }
    }