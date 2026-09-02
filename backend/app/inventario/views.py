# views.py
from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
from .models import Inventario
from .serializers import InventarioSerializer
from rest_framework.permissions import AllowAny

@api_view(['GET'])
@permission_classes([AllowAny]) #Sin persmisos
# @permission_classes([IsAuthenticated])

def listar_inventario(request):
    items = Inventario.objects.all().order_by('-creado_en')
    serializer = InventarioSerializer(items, many=True)
    return Response({
        'total_items': items.count(),
        'items': serializer.data
    })