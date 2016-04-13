.class public Lcom/withparadox2/tencenthotfixtest/hello/HelloContainer;
.super Ljava/lang/Object;
.source "HelloContainer.java"


# instance fields
.field a:I


# direct methods
.method public constructor <init>()V
    .locals 1

    .prologue
    .line 3
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 5
    const/4 v0, 0x1

    iput v0, p0, Lcom/withparadox2/tencenthotfixtest/hello/HelloContainer;->a:I

    const-class v0, Lcom/withparadox2/hackhelper/Helper;

    return-void
.end method


# virtual methods
.method public say()Ljava/lang/String;
    .locals 1

    .prologue
    .line 7
    const-string v0, "hello from patch successssssssssssss!"

    return-object v0
.end method
