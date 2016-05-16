package com.avaje.ebean;

import com.avaje.ebean.bean.BeanCollection;
import com.avaje.ebean.bean.CallStack;
import com.avaje.ebean.bean.EntityBeanIntercept;
import com.avaje.ebean.bean.ObjectGraphNode;
import com.avaje.ebean.cache.ServerCacheManager;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.DatabasePlatform;
import com.avaje.ebean.event.readaudit.ReadAuditLogger;
import com.avaje.ebean.event.readaudit.ReadAuditPrepare;
import com.avaje.ebean.meta.MetaInfoManager;
import com.avaje.ebean.plugin.SpiServer;
import com.avaje.ebean.text.csv.CsvReader;
import com.avaje.ebean.text.json.JsonContext;
import com.avaje.ebeaninternal.api.LoadBeanRequest;
import com.avaje.ebeaninternal.api.LoadManyRequest;
import com.avaje.ebeaninternal.api.ScopeTrans;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.api.SpiQuery;
import com.avaje.ebeaninternal.api.SpiTransaction;
import com.avaje.ebeaninternal.api.SpiTransactionScopeManager;
import com.avaje.ebeaninternal.api.TransactionEventTable;
import com.avaje.ebeaninternal.server.core.timezone.DataTimeZone;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;
import com.avaje.ebeaninternal.server.query.CQuery;
import com.avaje.ebeaninternal.server.transaction.RemoteTransactionEvent;

import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Test double for EbeanServer.
 */
public class TDEbeanServer implements SpiEbeanServer {

  public List deletedBeans = new ArrayList();

  public List savedBeans = new ArrayList();

  @Override
  public AutoTune getAutoTune() {
    return null;
  }

  @Override
  public SpiServer getPluginApi() {
    return null;
  }

  @Override
  public Object setBeanId(Object o, Object o1) {
    return null;
  }

  @Override
  public Transaction beginTransaction(TxScope txScope) {
    return null;
  }

  @Override
  public <T> List<Version<T>> findVersions(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> PagedList<T> findPagedList(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> int delete(Query<T> query, Transaction transaction) {
    return 0;
  }

  @Override
  public void findEach(SqlQuery sqlQuery, QueryEachConsumer<SqlRow> queryEachConsumer, Transaction transaction) {

  }

  @Override
  public void findEachWhile(SqlQuery sqlQuery, QueryEachWhileConsumer<SqlRow> queryEachWhileConsumer, Transaction transaction) {

  }

  @Override
  public boolean deletePermanent(Object o) throws OptimisticLockException {
    return false;
  }

  @Override
  public boolean deletePermanent(Object o, Transaction transaction) throws OptimisticLockException {
    return false;
  }

  @Override
  public int deleteAllPermanent(Collection<?> collection) throws OptimisticLockException {
    return 0;
  }

  @Override
  public int deleteAllPermanent(Collection<?> collection, Transaction transaction) throws OptimisticLockException {
    return 0;
  }

  @Override
  public int deletePermanent(Class<?> aClass, Object o) {
    return 0;
  }

  @Override
  public int deletePermanent(Class<?> aClass, Object o, Transaction transaction) {
    return 0;
  }

  @Override
  public int deleteAll(Collection<?> collection, Transaction transaction) throws OptimisticLockException {
    return 0;
  }

  @Override
  public int deleteAllPermanent(Class<?> aClass, Collection<?> collection) {
    return 0;
  }

  @Override
  public int deleteAllPermanent(Class<?> aClass, Collection<?> collection, Transaction transaction) {
    return 0;
  }

  @Override
  public DocumentStore docStore() {
    return null;
  }

  @Override
  public <T> T publish(Class<T> aClass, Object o, Transaction transaction) {
    return null;
  }

  @Override
  public <T> T publish(Class<T> aClass, Object o) {
    return null;
  }

  @Override
  public <T> List<T> publish(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> List<T> publish(Query<T> query) {
    return null;
  }

  @Override
  public <T> T draftRestore(Class<T> aClass, Object o, Transaction transaction) {
    return null;
  }

  @Override
  public <T> T draftRestore(Class<T> aClass, Object o) {
    return null;
  }

  @Override
  public <T> List<T> draftRestore(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> List<T> draftRestore(Query<T> query) {
    return null;
  }

  @Override
  public <T> Set<String> validateQuery(Query<T> query) {
    return null;
  }

  @Override
  public DataTimeZone getDataTimeZone() {
    return null;
  }

  @Override
  public ReadAuditPrepare getReadAuditPrepare() {
    return null;
  }

  @Override
  public ReadAuditLogger getReadAuditLogger() {
    return null;
  }

  @Override
  public BeanDescriptor<?> getBeanDescriptorByQueueId(String s) {
    return null;
  }

  @Override
  public boolean isUpdateAllPropertiesInBatch() {
    return false;
  }

  @Override
  public void shutdown(boolean shutdownDataSource, boolean deregisterDriver) {

  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public ExpressionFactory getExpressionFactory() {
    return null;
  }

  @Override
  public MetaInfoManager getMetaInfoManager() {
    return null;
  }

  @Override
  public BeanState getBeanState(Object bean) {
    return null;
  }

  @Override
  public Object getBeanId(Object bean) {
    return null;
  }

  @Override
  public Map<String, ValuePair> diff(Object a, Object b) {
    return null;
  }

  @Override
  public <T> T createEntityBean(Class<T> type) {
    return null;
  }

  @Override
  public <T> CsvReader<T> createCsvReader(Class<T> beanType) {
    return null;
  }

  @Override
  public <T> Query<T> createNamedQuery(Class<T> beanType, String namedQuery) {
    return null;
  }

  @Override
  public <T> Query<T> createQuery(Class<T> beanType, String query) {
    return null;
  }

  @Override
  public <T> Query<T> createQuery(Class<T> beanType) {
    return null;
  }

  @Override
  public <T> Query<T> find(Class<T> beanType) {
    return null;
  }

  @Override
  public Object nextId(Class<?> beanType) {
    return null;
  }

  @Override
  public <T> Filter<T> filter(Class<T> beanType) {
    return null;
  }

  @Override
  public <T> void sort(List<T> list, String sortByClause) {

  }

  @Override
  public <T> Update<T> createNamedUpdate(Class<T> beanType, String namedUpdate) {
    return null;
  }

  @Override
  public <T> Update<T> createUpdate(Class<T> beanType, String ormUpdate) {
    return null;
  }

  @Override
  public SqlQuery createSqlQuery(String sql) {
    return null;
  }

  @Override
  public SqlQuery createNamedSqlQuery(String namedQuery) {
    return null;
  }

  @Override
  public SqlUpdate createSqlUpdate(String sql) {
    return null;
  }

  @Override
  public CallableSql createCallableSql(String callableSql) {
    return null;
  }

  @Override
  public SqlUpdate createNamedSqlUpdate(String namedQuery) {
    return null;
  }

  @Override
  public void register(TransactionCallback transactionCallback) throws PersistenceException {

  }

  @Override
  public Transaction createTransaction() {
    return new TDTransaction();
  }

  @Override
  public Transaction createTransaction(TxIsolation isolation) {
    return new TDTransaction();
  }

  @Override
  public Transaction beginTransaction() {
    return new TDTransaction();
  }

  @Override
  public Transaction beginTransaction(TxIsolation isolation) {
    return new TDTransaction();
  }

  @Override
  public Transaction currentTransaction() {
    return new TDTransaction();
  }

  @Override
  public void commitTransaction() {

  }

  @Override
  public void rollbackTransaction() {

  }

  @Override
  public void endTransaction() {

  }

  @Override
  public void refresh(Object bean) {

  }

  @Override
  public void refreshMany(Object bean, String propertyName) {

  }

  @Override
  public <T> T find(Class<T> beanType, Object id) {
    return null;
  }

  @Override
  public <T> T getReference(Class<T> beanType, Object id) {
    return null;
  }

  @Override
  public <T> int findRowCount(Query<T> query, Transaction transaction) {
    return 0;
  }

  @Override
  public <T> List<Object> findIds(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> QueryIterator<T> findIterate(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> void findEach(Query<T> query, QueryEachConsumer<T> consumer, Transaction transaction) {

  }

  @Override
  public <T> void findEachWhile(Query<T> query, QueryEachWhileConsumer<T> consumer, Transaction transaction) {

  }

  @Override
  public <T> List<T> findList(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> FutureRowCount<T> findFutureRowCount(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> FutureIds<T> findFutureIds(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> FutureList<T> findFutureList(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> PagedList<T> findPagedList(Query<T> query, Transaction transaction, int pageIndex, int pageSize) {
    return null;
  }

  @Override
  public <T> Set<T> findSet(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> Map<?, T> findMap(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> T findUnique(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public List<SqlRow> findList(SqlQuery query, Transaction transaction) {
    return null;
  }

  @Override
  public SqlRow findUnique(SqlQuery query, Transaction transaction) {
    return null;
  }

  @Override
  public void save(Object bean) throws OptimisticLockException {
    savedBeans.add(bean);
  }

  @Override
  public int saveAll(Collection<?> beans) throws OptimisticLockException {
    savedBeans.addAll(beans);
    return 0;
  }

  @Override
  public boolean delete(Object bean) throws OptimisticLockException {
    deletedBeans.add(bean);
    return true;
  }

  @Override
  public int deleteAll(Collection<?> c) throws OptimisticLockException {
    deletedBeans.addAll(c);
    return 0;
  }

  @Override
  public int delete(Class<?> beanType, Object id) {
    return 0;
  }

  @Override
  public int delete(Class<?> beanType, Object id, Transaction transaction) {
    return 0;
  }

  @Override
  public int deleteAll(Class<?> beanType, Collection<?> ids) {
    return 0;
  }

  @Override
  public int deleteAll(Class<?> beanType, Collection<?> ids, Transaction transaction) {
    return 0;
  }

  @Override
  public int execute(SqlUpdate sqlUpdate) {
    return 0;
  }

  @Override
  public int execute(Update<?> update) {
    return 0;
  }

  @Override
  public int execute(Update<?> update, Transaction t) {
    return 0;
  }

  @Override
  public int execute(CallableSql callableSql) {
    return 0;
  }

  @Override
  public void externalModification(String tableName, boolean inserted, boolean updated, boolean deleted) {

  }

  @Override
  public <T> T find(Class<T> beanType, Object uid, Transaction transaction) {
    return null;
  }

  @Override
  public void save(Object bean, Transaction transaction) throws OptimisticLockException {
    savedBeans.add(bean);
  }

  @Override
  public int saveAll(Collection<?> beans, Transaction transaction) throws OptimisticLockException {
    savedBeans.addAll(beans);
    return 0;
  }

  @Override
  public void markAsDirty(Object bean) {

  }

  @Override
  public void update(Object bean) throws OptimisticLockException {

  }

  @Override
  public void update(Object bean, Transaction t) throws OptimisticLockException {

  }

  @Override
  public void update(Object bean, Transaction transaction, boolean deleteMissingChildren) throws OptimisticLockException {

  }

  @Override
  public void updateAll(Collection<?> beans) throws OptimisticLockException {

  }

  @Override
  public void updateAll(Collection<?> beans, Transaction transaction) throws OptimisticLockException {

  }

  @Override
  public void insert(Object bean) {

  }

  @Override
  public void insert(Object bean, Transaction t) {

  }

  @Override
  public void insertAll(Collection<?> beans) {

  }

  @Override
  public void insertAll(Collection<?> beans, Transaction t) {

  }

  @Override
  public int deleteManyToManyAssociations(Object ownerBean, String propertyName) {
    return 0;
  }

  @Override
  public int deleteManyToManyAssociations(Object ownerBean, String propertyName, Transaction t) {
    return 0;
  }

  @Override
  public void saveManyToManyAssociations(Object ownerBean, String propertyName) {

  }

  @Override
  public void saveManyToManyAssociations(Object ownerBean, String propertyName, Transaction t) {

  }

  @Override
  public void saveAssociation(Object ownerBean, String propertyName) {

  }

  @Override
  public void saveAssociation(Object ownerBean, String propertyName, Transaction t) {

  }

  @Override
  public boolean delete(Object bean, Transaction t) throws OptimisticLockException {
    return false;
  }

  @Override
  public int execute(SqlUpdate updSql, Transaction t) {
    return 0;
  }

  @Override
  public int execute(CallableSql callableSql, Transaction t) {
    return 0;
  }

  @Override
  public void execute(TxScope scope, TxRunnable r) {

  }

  @Override
  public void execute(TxRunnable r) {

  }

  @Override
  public <T> T execute(TxScope scope, TxCallable<T> c) {
    return null;
  }

  @Override
  public <T> T execute(TxCallable<T> c) {
    return null;
  }

  @Override
  public ServerCacheManager getServerCacheManager() {
    return null;
  }

  @Override
  public BackgroundExecutor getBackgroundExecutor() {
    return null;
  }

  @Override
  public JsonContext json() {
    return null;
  }


  @Override
  public void shutdownManaged() {

  }

  @Override
  public boolean isCollectQueryOrigins() {
    return false;
  }

  @Override
  public ServerConfig getServerConfig() {
    return null;
  }

  @Override
  public DatabasePlatform getDatabasePlatform() {
    return null;
  }

  @Override
  public CallStack createCallStack() {
    return null;
  }

  @Override
  public PersistenceContextScope getPersistenceContextScope(SpiQuery<?> query) {
    return null;
  }

  @Override
  public void clearQueryStatistics() {

  }

  @Override
  public List<BeanDescriptor<?>> getBeanDescriptors() {
    return null;
  }

  @Override
  public <T> BeanDescriptor<T> getBeanDescriptor(Class<T> type) {
    return null;
  }

  @Override
  public BeanDescriptor<?> getBeanDescriptorById(String descriptorId) {
    return null;
  }

  @Override
  public List<BeanDescriptor<?>> getBeanDescriptors(String tableName) {
    return null;
  }

  @Override
  public void externalModification(TransactionEventTable event) {

  }

  @Override
  public SpiTransaction createServerTransaction(boolean isExplicit, int isolationLevel) {
    return null;
  }

  @Override
  public SpiTransaction getCurrentServerTransaction() {
    return null;
  }

  TDTransaction scopedTransaction;

  protected TDTransaction testGetScopedTransaction() {
    return scopedTransaction;
  }

  @Override
  public ScopeTrans createScopeTrans(TxScope txScope) {

    boolean rollbackOnChecked = false;
    boolean created = true;
    scopedTransaction = new TDTransaction();
    SpiTransaction suspendedTransaction = null;
    SpiTransactionScopeManager scopeMgr = null;

    if (txScope.isSkipGeneratedKeys()) {
      txScope.setSkipGeneratedKeys();
    }

    return new ScopeTrans(rollbackOnChecked, created, scopedTransaction, txScope, suspendedTransaction, scopeMgr);
  }

  @Override
  public SpiTransaction createQueryTransaction() {
    return null;
  }

  @Override
  public void remoteTransactionEvent(RemoteTransactionEvent event) {

  }

  @Override
  public <T> CQuery<T> compileQuery(Query<T> query, Transaction t) {
    return null;
  }

  @Override
  public <T> List<Object> findIdsWithCopy(Query<T> query, Transaction t) {
    return null;
  }

  @Override
  public <T> int findRowCountWithCopy(Query<T> query, Transaction t) {
    return 0;
  }

  @Override
  public void loadBean(LoadBeanRequest loadRequest) {

  }

  @Override
  public void loadMany(LoadManyRequest loadRequest) {

  }

  @Override
  public int getLazyLoadBatchSize() {
    return 0;
  }

  @Override
  public boolean isSupportedType(Type genericType) {
    return false;
  }

  @Override
  public void collectQueryStats(ObjectGraphNode objectGraphNode, long loadedBeanCount, long timeMicros) {

  }

  @Override
  public void loadMany(BeanCollection<?> collection, boolean onlyIds) {

  }

  @Override
  public void loadBean(EntityBeanIntercept ebi) {

  }
}
